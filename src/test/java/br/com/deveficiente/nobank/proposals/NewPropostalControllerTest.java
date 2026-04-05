package br.com.deveficiente.nobank.proposals;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableWireMock({
    @ConfigureWireMock(
        name = "financialAnalysisClient",
        baseUrlProperties = "spring.cloud.openfeign.client.config.financialAnalysisClient.url"
    )
})
class NewPropostalControllerTest {

    @InjectWireMock("financialAnalysisClient")
    private WireMockServer wireMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProposalRepository repository;

    @BeforeEach
    void setup() {
        wireMock.resetAll();
        repository.deleteAll();
    }

    @Test
    void shouldCreateProposalWhenEligible() throws Exception {
        // scenario
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "idProposta": "any-id",
                                            "resultadoSolicitacao": "SEM_RESTRICAO"
                                        }
                                        """))
        );

        // action & validation
        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "document": "935.411.347-80",
                                    "email": "joao@email.com",
                                    "name": "Joao da Silva",
                                    "address": "Rua das Tabajaras, 123",
                                    "salary": 5000.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        // verify financial analysis was called
        wireMock.verify(1, postRequestedFor(urlEqualTo("/api/solicitacao")));
    }

    @Test
    void shouldCreateProposalWhenNotEligible() throws Exception {
        // scenario
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "idProposta": "any-id",
                                            "resultadoSolicitacao": "COM_RESTRICAO"
                                        }
                                        """))
        );

        // action & validation
        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "document": "935.411.347-80",
                                    "email": "joao@email.com",
                                    "name": "Joao da Silva",
                                    "address": "Rua das Tabajaras, 123",
                                    "salary": 5000.00
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectDuplicateProposal() throws Exception {
        // scenario: create first proposal
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "idProposta": "any-id",
                                            "resultadoSolicitacao": "SEM_RESTRICAO"
                                        }
                                        """))
        );

        String requestBody = """
                {
                    "document": "935.411.347-80",
                    "email": "joao@email.com",
                    "name": "Joao da Silva",
                    "address": "Rua das Tabajaras, 123",
                    "salary": 5000.00
                }
                """;

        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // action: try to create duplicate
        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldRetryAndCreateProposalWhenServerFailsThenRecovers() throws Exception {
        // scenario: first 2 calls return 500, third returns 200
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .inScenario("retry")
                        .whenScenarioStateIs(com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED)
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        { "error": "Internal Server Error" }
                                        """))
                        .willSetStateTo("FIRST_RETRY")
        );
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .inScenario("retry")
                        .whenScenarioStateIs("FIRST_RETRY")
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        { "error": "Internal Server Error" }
                                        """))
                        .willSetStateTo("SECOND_RETRY")
        );
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .inScenario("retry")
                        .whenScenarioStateIs("SECOND_RETRY")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        {
                                            "idProposta": "any-id",
                                            "resultadoSolicitacao": "SEM_RESTRICAO"
                                        }
                                        """))
        );

        // action & validation: should succeed thanks to retry
        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "document": "935.411.347-80",
                                    "email": "joao@email.com",
                                    "name": "Joao da Silva",
                                    "address": "Rua das Tabajaras, 123",
                                    "salary": 5000.00
                                }
                                """))
                .andExpect(status().isCreated());

        // verify: 3 calls were made (2 failures + 1 success)
        wireMock.verify(3, postRequestedFor(urlEqualTo("/api/solicitacao")));
    }

    @Test
    void shouldFailAfterAllRetriesExhausted() throws Exception {
        // scenario: all calls return 500
        wireMock.stubFor(
                WireMock.post(urlEqualTo("/api/solicitacao"))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        { "error": "Internal Server Error" }
                                        """))
        );

        // action & validation: should fail after 3 attempts with 503
        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "document": "935.411.347-80",
                                    "email": "joao@email.com",
                                    "name": "Joao da Silva",
                                    "address": "Rua das Tabajaras, 123",
                                    "salary": 5000.00
                                }
                                """))
                .andExpect(status().isServiceUnavailable());

        // verify: 3 calls were made (maxAttempts = 3)
        wireMock.verify(3, postRequestedFor(urlEqualTo("/api/solicitacao")));
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "document": "",
                                    "email": "invalid-email",
                                    "name": "",
                                    "address": "",
                                    "salary": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}

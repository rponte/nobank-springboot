package br.com.deveficiente.nobank.proposals;

import br.com.deveficiente.nobank.proposals.integration.FinancialAnalysisClient;
import br.com.deveficiente.nobank.proposals.integration.SubmitForAnalysisRequest;
import br.com.deveficiente.nobank.proposals.integration.SubmitForAnalysisResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
public class NewPropostalController {

    private static final Logger logger = LoggerFactory.getLogger(NewPropostalController.class);

    @Autowired
    private ProposalRepository repository;
    @Autowired
    private FinancialAnalysisClient financialClient;

    @Transactional
    @PostMapping("/api/v1/proposals")
    public ResponseEntity<?> create(@Valid @RequestBody NewProposalRequest request, UriComponentsBuilder uriBuilder) {

        if (repository.existsByDocument(request.getDocument())) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "Proposal already exists");
        }

        Proposal proposal = request.toModel();
        repository.save(proposal);

        SubmitForAnalysisResponse response = financialClient
                    .submitForAnalysis(new SubmitForAnalysisRequest((proposal)));

        ProposalStatus status = response.toModel();
        proposal.updateStatus(status); // thanks JPA: persistence context

        URI location = uriBuilder
                .path("/api/v1/proposals/{id}")
                .buildAndExpand(proposal.getId()).toUri();

        return ResponseEntity.created(location)
                    .body(new NewProposalResponse(proposal));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException ex) {
        logger.error("Error calling external service: {}", ex.getMessage());
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status != null && status.is4xxClientError()) {
            return ResponseEntity.status(status).body(ex.contentUTF8());
        }
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("External service unavailable");
    }

}

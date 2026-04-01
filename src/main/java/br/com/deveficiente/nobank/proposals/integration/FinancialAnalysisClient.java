package br.com.deveficiente.nobank.proposals.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "financialAnalysisClient")
public interface FinancialAnalysisClient {

    @PostMapping("/api/solicitacao")
    public SubmitForAnalysisResponse submitForAnalysis(@RequestBody SubmitForAnalysisRequest request);

}

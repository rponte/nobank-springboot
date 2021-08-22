package br.com.zup.edu.nobank.proposals.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "financialAnalysisClient", url = "${financialAnalysisClient.targetUrl}")
public interface FinancialAnalysisClient {

    @PostMapping("/api/solicitacao")
    public SubmitForAnalysisResponse submitForAnalysis(@RequestBody SubmitForAnalysisRequest request);

}

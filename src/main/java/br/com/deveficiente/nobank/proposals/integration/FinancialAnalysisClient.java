package br.com.deveficiente.nobank.proposals.integration;

import feign.FeignException.FeignServerException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@FeignClient(name = "financialAnalysisClient")
public interface FinancialAnalysisClient {

    @Retryable(
        retryFor = { FeignServerException.class, IOException.class },
        maxAttempts = 3,
        backoff = @Backoff(
                delay = 1000,
                multiplier = 2,
                maxDelay = 10000,
                random = true
        )
    )
    @PostMapping("/api/solicitacao")
    SubmitForAnalysisResponse submitForAnalysis(@RequestBody SubmitForAnalysisRequest request);

}

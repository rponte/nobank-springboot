package br.com.deveficiente.nobank.cards.integration;

import feign.FeignException.FeignServerException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(value = "cardsClient")
public interface CardsClient {

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
    @RequestMapping(method = GET, path = "/api/cards")
    CardDataResponse findCardByProposalId(@RequestParam UUID proposalId);
}

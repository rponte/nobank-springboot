package br.com.deveficiente.nobank.cards.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(value = "cardsClient")
public interface CardsClient {

    @RequestMapping(method = GET, path = "/api/cards")
    public CardDataResponse findCardByProposalId(@RequestParam UUID proposalId);
}

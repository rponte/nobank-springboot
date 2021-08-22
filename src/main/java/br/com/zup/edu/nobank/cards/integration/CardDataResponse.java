package br.com.zup.edu.nobank.cards.integration;

import br.com.zup.edu.nobank.cards.Card;

import java.util.UUID;

public class CardDataResponse {

    private String cardNumber;
    private String proposalId;
    private String cardHolderName;

    public Card toModel() {
        return new Card(UUID.fromString(proposalId), cardNumber, cardHolderName);
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public String getProposalId() {
        return proposalId;
    }
    public String getCardHolderName() {
        return cardHolderName;
    }

    @Override
    public String toString() {
        return "CardDataResponse{" +
                "cardNumber='" + cardNumber + '\'' +
                ", proposalId='" + proposalId + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                '}';
    }
}

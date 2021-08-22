package br.com.zup.edu.nobank.cards;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID proposalId; // TIP: this approach does not add a FK in the database

    @NotBlank
    @Column(nullable = false)
    private String cardNumber;

    @NotBlank
    @Column(nullable = false)
    private String cardHolderName;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Card(UUID proposalId, String cardNumber, String cardHolderName) {
        this.proposalId = proposalId;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public String getCardHolderName() {
        return cardHolderName;
    }
    public UUID getProposalId() {
        return proposalId;
    }
    public void setProposalId(UUID proposalId) {
        this.proposalId = proposalId;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", proposalId=" + proposalId +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

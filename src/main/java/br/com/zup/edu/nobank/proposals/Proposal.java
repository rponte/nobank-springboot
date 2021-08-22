package br.com.zup.edu.nobank.proposals;

import br.com.zup.edu.nobank.cards.Card;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Proposal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String document;

    private String email;
    private String name;
    private String address;
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status = ProposalStatus.NOT_ELIGIBLE;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Deprecated
    protected Proposal(){}

    public Proposal(String document, String email, String name, String address, BigDecimal salary) {
        this.document = document;
        this.email = email;
        this.name = name;
        this.address = address;
        this.salary = salary;
    }

    public UUID getId() {
        return id;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getDocument() {
        return document;
    }
    public String getName() {
        return name;
    }

    /**
     * Updates the proposal status
     */
    public void updateStatus(ProposalStatus status) {

        if (status == null)
            throw new IllegalArgumentException("Status can not be null");

        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Attaches a new created and active card to this proposal
     */
    public void attachTo(Card activeCard) {
        if (activeCard == null) {
            throw new IllegalArgumentException("Impossible to attach this proposal to a card: card can not be null");
        }
        /**
         * TIP:
         * Fortunately, for this domain, a proposal does not need to know which card it's attached to,
         * that's why there's no association in proposal-side (I mean, an @OneToOne mapping).
         */
        activeCard.setProposalId(this.id); // Just to keep some consistence between the entities.
        this.updateStatus(ProposalStatus.ELIGIBLE_WITH_ATTACHED_CARD);
    }
}

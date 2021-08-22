package br.com.zup.edu.nobank.proposals;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewProposalResponse {

    private UUID id;
    private LocalDateTime createdAt;

    public NewProposalResponse(Proposal proposal) {
        this.id = proposal.getId();
        this.createdAt = proposal.getCreatedAt();
    }

    public UUID getId() {
        return id;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}

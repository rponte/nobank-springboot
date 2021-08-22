package br.com.zup.edu.nobank.proposals;

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

    public void updateStatus(ProposalStatus status) {

        if (status == null)
            throw new IllegalArgumentException("Status can not be null");

        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

}

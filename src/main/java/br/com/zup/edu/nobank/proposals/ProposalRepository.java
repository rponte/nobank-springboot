package br.com.zup.edu.nobank.proposals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    public boolean existsByDocument(String document);

    public List<Proposal> findTop50ByStatusOrderByCreatedAtAsc(ProposalStatus eligible);

}

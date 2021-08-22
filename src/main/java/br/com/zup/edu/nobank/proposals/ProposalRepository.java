package br.com.zup.edu.nobank.proposals;

import org.hibernate.LockOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    public boolean existsByDocument(String document);

    /**
     * TIP:
     * This configuration solves 2 issues:
     *
     * 1) OutOfMemoryError: we can limit the results of query methods by using the first or top keywords
     * or Pageable parameter:
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result
     *
     * 2) Race Condition: using SKIP LOCKED here allows this feature to work in clustered environments;
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "javax.persistence.lock.timeout", value = (LockOptions.SKIP_LOCKED + ""))
    })
    public List<Proposal> findTop50ByStatusOrderByCreatedAtAsc(ProposalStatus eligible);

}

package br.com.zup.edu.nobank.cards;

import br.com.zup.edu.nobank.cards.integration.CardDataResponse;
import br.com.zup.edu.nobank.cards.integration.CardsClient;
import br.com.zup.edu.nobank.proposals.Proposal;
import br.com.zup.edu.nobank.proposals.ProposalRepository;
import br.com.zup.edu.nobank.proposals.ProposalStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class AttachCardsToProposalsJob {

    private static final long ONE_MINUTE = 60*1000;
    private static final Logger logger = LoggerFactory.getLogger(AttachCardsToProposalsJob.class);

    @Autowired
    private CardsClient cardsClient;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private TransactionTemplate transactionManager;

    /**
     * Scenarios:
     *
     * 1. Naive solution
     * 2. Top50 (aka pagination) only = finishes too early
     * 2.1. Top50 + loop = long-running transaction
     * 3. Top50 sem @Transactional = inconsistência nos dados
     * 4. Top50 + transactionManager
     * 5. cluster? Top50 + transactionManager + synchronized = fail
     * 6. cluster? Top50 + transactionManager + FOR UPDATE = work but 1 node only
     * 7. cluster? Top50 + transactionManager + FOR UPDATE SKIP LOCKED = WOW!
     * 8. performance? Hibernate batch size
     * 9. error handling? timeout, retries, idempotence
     * 10. What if...
     */
    @Scheduled(fixedDelay = ONE_MINUTE)
    public void execute() {

        logger.info("Verifying eligible proposals that have no attached cards yet...");

        boolean pending = true;
        while (pending) {
            //noinspection ConstantConditions
            pending = transactionManager.execute(transactionStatus -> {

                List<Proposal> eligibleProposals = proposalRepository.findTop50ByStatusOrderByCreatedAtAsc(ProposalStatus.ELIGIBLE);
                if (eligibleProposals.isEmpty()) {
                    return false;
                }

                eligibleProposals.forEach(proposal -> {

                    CardDataResponse cardData = cardsClient.findCardByProposalId(proposal.getId());

                    Card newCard = cardData.toModel();
                    cardRepository.save(newCard);

                    logger.info("Attaching card '{}' to proposal '{}'", newCard.getCardNumber(), proposal.getId());
                    proposal.attachTo(newCard);
                    proposalRepository.save(proposal);
                });

                return true;
            });
        }
    }
}

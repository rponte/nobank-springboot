package br.com.deveficiente.nobank;

import br.com.deveficiente.nobank.cards.Card;
import br.com.deveficiente.nobank.cards.CardRepository;
import br.com.deveficiente.nobank.cards.integration.CardDataResponse;
import br.com.deveficiente.nobank.cards.integration.CardsClient;
import br.com.deveficiente.nobank.proposals.Proposal;
import br.com.deveficiente.nobank.proposals.ProposalRepository;
import br.com.deveficiente.nobank.proposals.ProposalStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(
        fixedDelay = ONE_MINUTE
    )
    public void execute() {

        logger.info("Verifying eligible proposals that have no attached cards yet...");

        List<Proposal> eligibleProposals = proposalRepository.findAllByStatusOrderByCreatedAtAsc(ProposalStatus.ELIGIBLE);
        eligibleProposals.forEach(proposal -> {

            CardDataResponse cardData = cardsClient.findCardByProposalId(proposal.getId());

            Card newCard = cardData.toModel();
            cardRepository.save(newCard);

            logger.info("Attaching card '{}' to proposal '{}'", newCard.getCardNumber(), proposal.getId());
            proposal.attachTo(newCard);
            proposalRepository.save(proposal);
        });
    }
}

package br.com.zup.edu.nobank.proposals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;

import java.net.URI;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
public class NewPropostalController {

    @Autowired
    private ProposalRepository repository;

    @Transactional
    @PostMapping("/api/v1/proposals")
    public ResponseEntity<?> create(@Valid @RequestBody NewProposalRequest request, UriComponentsBuilder uriBuilder) {

        if (repository.existsByDocument(request.getDocument())) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "Proposal already exists");
        }

        Proposal proposal = request.toModel();
        repository.save(proposal);

        URI location = uriBuilder
                .path("/api/v1/proposals/{id}")
                .buildAndExpand(proposal.getId()).toUri();

        return ResponseEntity.created(location)
                    .body(new NewProposalResponse(proposal));
    }

}

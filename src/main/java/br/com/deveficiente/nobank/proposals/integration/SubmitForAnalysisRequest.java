package br.com.deveficiente.nobank.proposals.integration;

import br.com.deveficiente.nobank.proposals.Proposal;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubmitForAnalysisRequest {

    @JsonProperty("documento")
    private String document;
    @JsonProperty("nome")
    private String customerName;
    @JsonProperty("idProposta")
    private String proposalId;

    public SubmitForAnalysisRequest(Proposal proposal) {
        this.document = proposal.getDocument();
        this.customerName = proposal.getName();
        this.proposalId = proposal.getId().toString();
    }

    public String getDocument() {
        return document;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getProposalId() {
        return proposalId;
    }

    @Override
    public String toString() {
        return "SubmitForAnalysisRequest{" +
                "document='" + document + '\'' +
                ", customerName='" + customerName + '\'' +
                ", proposalId='" + proposalId + '\'' +
                '}';
    }

}

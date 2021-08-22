package br.com.zup.edu.nobank.proposals.integration;

import br.com.zup.edu.nobank.proposals.ProposalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SubmitForAnalysisResponse {

    @JsonProperty("idProposta")
    private String proposalId;
    @JsonProperty("resultadoSolicitacao")
    private String result;

    public String getProposalId() {
        return proposalId;
    }
    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "SubmitForAnalysisResponse{" +
                "proposalId='" + proposalId + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public ProposalStatus toModel() {
        if ("SEM_RESTRICAO".equals(result)) {
            return ProposalStatus.ELIGIBLE;
        }
        return ProposalStatus.NOT_ELIGIBLE;
    }

}

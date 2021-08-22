package br.com.zup.edu.nobank.proposals;

import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class NewProposalRequest {

    @CPF @NotBlank
    private String document;
    @Email @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @Positive @NotNull
    private BigDecimal salary;

    public NewProposalRequest(String document, String email, String name, String address, BigDecimal salary) {
        this.document = document;
        this.email = email;
        this.name = name;
        this.address = address;
        this.salary = salary;
    }

    public String getDocument() {
        return document;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public BigDecimal getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "NewProposalRequest{" +
                "document='" + document + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", salary=" + salary +
                '}';
    }

    public Proposal toModel() {
        return new Proposal(document, email, name, address, salary);
    }
}

package ma.enset.ebank.dtos;

import lombok.Data;

/** Création d'un compte : type "CURRENT" (overDraft) ou "SAVING" (interestRate). */
@Data
public class NewAccountRequestDTO {
    private Long customerId;
    private String type;
    private double initialBalance;
    private double overDraft;
    private double interestRate;
}

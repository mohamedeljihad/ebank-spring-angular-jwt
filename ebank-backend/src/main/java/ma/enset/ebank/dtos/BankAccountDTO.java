package ma.enset.ebank.dtos;

import lombok.Data;

/** Classe mère des DTO de comptes ; "type" permet au client de distinguer les deux sous-types. */
@Data
public class BankAccountDTO {
    private String type;
}

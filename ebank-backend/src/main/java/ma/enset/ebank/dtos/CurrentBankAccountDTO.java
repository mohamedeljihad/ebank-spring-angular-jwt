package ma.enset.ebank.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.enset.ebank.enums.AccountStatus;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrentBankAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private String createdBy;
    private CustomerDTO customerDTO;
    private double overDraft;
}

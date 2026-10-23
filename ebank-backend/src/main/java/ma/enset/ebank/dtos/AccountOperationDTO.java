package ma.enset.ebank.dtos;

import lombok.Data;
import ma.enset.ebank.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
    private String performedBy;
}

package ma.enset.ebank.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.ebank.enums.OperationType;

import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    private String description;
    private String performedBy;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BankAccount bankAccount;
}

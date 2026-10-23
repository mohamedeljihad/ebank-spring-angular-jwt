package ma.enset.ebank.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.ebank.enums.AccountStatus;

import java.util.Date;
import java.util.List;

/** Héritage SINGLE_TABLE : une table BANK_ACCOUNT avec une colonne TYPE (CA / SA). */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 4)
@Data @NoArgsConstructor @AllArgsConstructor
public abstract class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String createdBy;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AccountOperation> accountOperations;
}

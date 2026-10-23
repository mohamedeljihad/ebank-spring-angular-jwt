package ma.enset.ebank.mappers;

import ma.enset.ebank.dtos.*;
import ma.enset.ebank.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/** Conversion entités <-> DTO (évite d'exposer les entités JPA et les cycles de sérialisation). */
@Service
public class BankAccountMapperImpl {

    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);
        return dto;
    }

    public Customer fromCustomerDTO(CustomerDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        return customer;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount account) {
        SavingBankAccountDTO dto = new SavingBankAccountDTO();
        BeanUtils.copyProperties(account, dto);
        dto.setCustomerDTO(fromCustomer(account.getCustomer()));
        dto.setType(account.getClass().getSimpleName());
        return dto;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO dto) {
        SavingAccount account = new SavingAccount();
        BeanUtils.copyProperties(dto, account);
        account.setCustomer(fromCustomerDTO(dto.getCustomerDTO()));
        return account;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount account) {
        CurrentBankAccountDTO dto = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(account, dto);
        dto.setCustomerDTO(fromCustomer(account.getCustomer()));
        dto.setType(account.getClass().getSimpleName());
        return dto;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO dto) {
        CurrentAccount account = new CurrentAccount();
        BeanUtils.copyProperties(dto, account);
        account.setCustomer(fromCustomerDTO(dto.getCustomerDTO()));
        return account;
    }

    public BankAccountDTO fromBankAccount(BankAccount account) {
        if (account instanceof SavingAccount s) return fromSavingBankAccount(s);
        return fromCurrentBankAccount((CurrentAccount) account);
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation op) {
        AccountOperationDTO dto = new AccountOperationDTO();
        BeanUtils.copyProperties(op, dto);
        return dto;
    }
}

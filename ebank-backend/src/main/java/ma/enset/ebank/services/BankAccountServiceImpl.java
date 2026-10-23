package ma.enset.ebank.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebank.dtos.*;
import ma.enset.ebank.entities.*;
import ma.enset.ebank.enums.AccountStatus;
import ma.enset.ebank.enums.OperationType;
import ma.enset.ebank.exceptions.*;
import ma.enset.ebank.mappers.BankAccountMapperImpl;
import ma.enset.ebank.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {
    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapperImpl dtoMapper;

    // ------------------------------------------------------------------ clients
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        customer.setCreatedBy(CurrentUser.username());
        return dtoMapper.fromCustomer(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer existing = customerRepository.findById(customerDTO.getId()).orElse(null);
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        customer.setCreatedBy(existing != null ? existing.getCreatedBy() : CurrentUser.username());
        return dtoMapper.fromCustomer(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long customerId) {
        // suppression en cascade manuelle : opérations, comptes puis client
        for (BankAccount account : bankAccountRepository.findByCustomerId(customerId)) {
            accountOperationRepository.deleteAll(accountOperationRepository.findByBankAccountId(account.getId()));
            bankAccountRepository.delete(account);
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream().map(dtoMapper::fromCustomer).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.searchCustomer("%" + keyword + "%").stream()
                .map(dtoMapper::fromCustomer).collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ comptes
    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(new Date());
        account.setBalance(initialBalance);
        account.setOverDraft(overDraft);
        account.setStatus(AccountStatus.CREATED);
        account.setCustomer(customer);
        account.setCreatedBy(CurrentUser.username());
        return dtoMapper.fromCurrentBankAccount(bankAccountRepository.save(account));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        SavingAccount account = new SavingAccount();
        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(new Date());
        account.setBalance(initialBalance);
        account.setInterestRate(interestRate);
        account.setStatus(AccountStatus.CREATED);
        account.setCustomer(customer);
        account.setCreatedBy(CurrentUser.username());
        return dtoMapper.fromSavingBankAccount(bankAccountRepository.save(account));
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        return dtoMapper.fromBankAccount(findAccount(accountId));
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream().map(dtoMapper::fromBankAccount).collect(Collectors.toList());
    }

    @Override
    public List<BankAccountDTO> customerAccounts(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId).stream()
                .map(dtoMapper::fromBankAccount).collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO updateAccountStatus(String accountId, String status) throws BankAccountNotFoundException {
        BankAccount account = findAccount(accountId);
        account.setStatus(AccountStatus.valueOf(status));
        return dtoMapper.fromBankAccount(bankAccountRepository.save(account));
    }

    // ------------------------------------------------------------------ opérations
    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount account = findAccount(accountId);
        if (account.getStatus() == AccountStatus.SUSPENDED)
            throw new IllegalStateException("Compte suspendu : opération impossible");
        double overDraft = account instanceof CurrentAccount ca ? ca.getOverDraft() : 0;
        if (account.getBalance() + overDraft < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        saveOperation(account, amount, OperationType.DEBIT, description);
        account.setBalance(account.getBalance() - amount);
        bankAccountRepository.save(account);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount account = findAccount(accountId);
        if (account.getStatus() == AccountStatus.SUSPENDED)
            throw new IllegalStateException("Compte suspendu : opération impossible");
        saveOperation(account, amount, OperationType.CREDIT, description);
        account.setBalance(account.getBalance() + amount);
        bankAccountRepository.save(account);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        return accountOperationRepository.findByBankAccountId(accountId).stream()
                .map(dtoMapper::fromAccountOperation).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount account = findAccount(accountId);
        Page<AccountOperation> ops = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO dto = new AccountHistoryDTO();
        dto.setAccountOperationDTOS(ops.getContent().stream().map(dtoMapper::fromAccountOperation).collect(Collectors.toList()));
        dto.setAccountId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setType(account.getClass().getSimpleName());
        dto.setCustomerName(account.getCustomer() != null ? account.getCustomer().getName() : null);
        dto.setCurrentPage(page);
        dto.setPageSize(size);
        dto.setTotalPages(ops.getTotalPages());
        return dto;
    }

    // ------------------------------------------------------------------ tableau de bord
    @Override
    @Transactional(readOnly = true)
    public DashboardDTO dashboard() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        List<AccountOperation> operations = accountOperationRepository.findAll();
        DashboardDTO d = new DashboardDTO();
        d.setTotalCustomers(customerRepository.count());
        d.setTotalAccounts(accounts.size());
        d.setTotalOperations(operations.size());
        d.setTotalBalance(accounts.stream().mapToDouble(BankAccount::getBalance).sum());
        d.setAccountsByType(accounts.stream().collect(Collectors.groupingBy(a -> a.getClass().getSimpleName(), TreeMap::new, Collectors.counting())));
        d.setAccountsByStatus(accounts.stream().collect(Collectors.groupingBy(a -> a.getStatus().name(), TreeMap::new, Collectors.counting())));
        d.setOperationsAmountByType(operations.stream().collect(Collectors.groupingBy(o -> o.getType().name(), TreeMap::new, Collectors.summingDouble(AccountOperation::getAmount))));
        SimpleDateFormat month = new SimpleDateFormat("yyyy-MM");
        Map<String, Map<String, Double>> byMonth = new TreeMap<>();
        for (AccountOperation o : operations) {
            byMonth.computeIfAbsent(month.format(o.getOperationDate()), k -> new TreeMap<>())
                    .merge(o.getType().name(), o.getAmount(), Double::sum);
        }
        d.setOperationsByMonth(byMonth);
        Map<String, Double> perCustomer = accounts.stream().filter(a -> a.getCustomer() != null)
                .collect(Collectors.groupingBy(a -> a.getCustomer().getName(), Collectors.summingDouble(BankAccount::getBalance)));
        LinkedHashMap<String, Double> top = perCustomer.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        d.setTopCustomersBalance(top);
        return d;
    }

    // ------------------------------------------------------------------ utilitaires
    private BankAccount findAccount(String accountId) throws BankAccountNotFoundException {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
    }

    private void saveOperation(BankAccount account, double amount, OperationType type, String description) {
        AccountOperation op = new AccountOperation();
        op.setType(type);
        op.setAmount(amount);
        op.setDescription(description);
        op.setOperationDate(new Date());
        op.setBankAccount(account);
        op.setPerformedBy(CurrentUser.username());
        accountOperationRepository.save(op);
    }
}

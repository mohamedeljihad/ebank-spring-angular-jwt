package ma.enset.ebank.web;

import lombok.AllArgsConstructor;
import ma.enset.ebank.dtos.*;
import ma.enset.ebank.exceptions.*;
import ma.enset.ebank.services.BankAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestController {
    private final BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountList();
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public BankAccountDTO newAccount(@RequestBody NewAccountRequestDTO request) throws CustomerNotFoundException {
        if ("SAVING".equalsIgnoreCase(request.getType()))
            return bankAccountService.saveSavingBankAccount(request.getInitialBalance(), request.getInterestRate(), request.getCustomerId());
        return bankAccountService.saveCurrentBankAccount(request.getInitialBalance(), request.getOverDraft(), request.getCustomerId());
    }

    @PatchMapping("/accounts/{accountId}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public BankAccountDTO updateStatus(@PathVariable String accountId, @RequestParam String status) throws BankAccountNotFoundException {
        return bankAccountService.updateAccountStatus(accountId, status);
    }

    @GetMapping("/accounts/{accountId}/operations")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                               @RequestParam(name = "size", defaultValue = "5") int size)
            throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    @PostMapping("/accounts/debit")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void transfer(@RequestBody TransferRequestDTO request) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(request.getAccountSource(), request.getAccountDestination(), request.getAmount());
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public DashboardDTO dashboard() {
        return bankAccountService.dashboard();
    }
}

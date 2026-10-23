package ma.enset.ebank.chatbot;

import lombok.AllArgsConstructor;
import ma.enset.ebank.dtos.*;
import ma.enset.ebank.services.BankAccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/** Outils exposés à l'agent : il peut interroger les vraies données de la banque. */
@Component
@AllArgsConstructor
public class BankTools {
    private final BankAccountService bankAccountService;

    @Tool(description = "Rechercher des clients par nom ou email")
    public List<CustomerDTO> searchCustomers(@ToolParam(description = "nom ou partie du nom") String keyword) {
        return bankAccountService.searchCustomers(keyword);
    }

    @Tool(description = "Lister les comptes bancaires d'un client à partir de son identifiant")
    public List<BankAccountDTO> customerAccounts(@ToolParam(description = "identifiant du client") Long customerId) {
        return bankAccountService.customerAccounts(customerId);
    }

    @Tool(description = "Obtenir le solde, le type et les 5 dernières opérations d'un compte")
    public AccountHistoryDTO accountSummary(@ToolParam(description = "identifiant (UUID) du compte") String accountId) {
        try {
            return bankAccountService.getAccountHistory(accountId, 0, 5);
        } catch (Exception e) {
            throw new IllegalArgumentException("Compte introuvable : " + accountId);
        }
    }

    @Tool(description = "Statistiques globales de la banque (clients, comptes, soldes, opérations)")
    public DashboardDTO statistics() {
        return bankAccountService.dashboard();
    }
}

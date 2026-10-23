package ma.enset.ebank.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class DashboardDTO {
    private long totalCustomers;
    private long totalAccounts;
    private long totalOperations;
    private double totalBalance;
    private Map<String, Long> accountsByType;
    private Map<String, Long> accountsByStatus;
    private Map<String, Double> operationsAmountByType;
    /** montant des opérations par mois (yyyy-MM) et par type */
    private Map<String, Map<String, Double>> operationsByMonth;
    /** top clients par solde total */
    private Map<String, Double> topCustomersBalance;
}

package ma.enset.ebank;

import ma.enset.ebank.dtos.BankAccountDTO;
import ma.enset.ebank.dtos.CurrentBankAccountDTO;
import ma.enset.ebank.dtos.CustomerDTO;
import ma.enset.ebank.dtos.SavingBankAccountDTO;
import ma.enset.ebank.security.AccountService;
import ma.enset.ebank.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankApplication.class, args);
    }

    /** Utilisateurs et rôles initiaux (mots de passe encodés BCrypt). */
    @Bean
    CommandLineRunner initUsers(AccountService accountService) {
        return args -> {
            accountService.addNewRole("USER");
            accountService.addNewRole("ADMIN");
            accountService.addNewUser("user1", "1234", "user1@gmail.com", "1234");
            accountService.addNewUser("user2", "1234", "user2@gmail.com", "1234");
            accountService.addNewUser("admin", "1234", "admin@gmail.com", "1234");
            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("admin", "USER");
            accountService.addRoleToUser("admin", "ADMIN");
        };
    }

    /** Test de la couche DAO / service : clients, comptes et opérations aléatoires. */
    @Bean
    CommandLineRunner initData(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Hassan", "Imane", "Mohamed", "Yassine", "Salma").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name.toLowerCase() + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> accounts = bankAccountService.bankAccountList();
            for (BankAccountDTO account : accounts) {
                String id = account instanceof SavingBankAccountDTO s ? s.getId() : ((CurrentBankAccountDTO) account).getId();
                for (int i = 0; i < 10; i++) {
                    try {
                        bankAccountService.credit(id, 10000 + Math.random() * 120000, "Credit");
                        bankAccountService.debit(id, 1000 + Math.random() * 9000, "Debit");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Comptes créés : " + accounts.size());
        };
    }
}

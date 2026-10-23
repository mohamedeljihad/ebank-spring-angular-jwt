package ma.enset.ebank.repositories;

import ma.enset.ebank.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where lower(c.name) like lower(:kw) or lower(c.email) like lower(:kw)")
    List<Customer> searchCustomer(@Param("kw") String keyword);
}

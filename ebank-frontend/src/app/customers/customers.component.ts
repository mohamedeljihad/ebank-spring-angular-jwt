import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { AuthService } from '../services/auth.service';
import { Customer } from '../model/customer.model';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  keyword = '';
  errorMessage = '';

  constructor(private customerService: CustomerService, public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.handleSearch();
  }

  handleSearch(): void {
    this.customerService.searchCustomers(this.keyword).subscribe({
      next: data => (this.customers = data),
      error: err => (this.errorMessage = err.error?.message ?? err.message)
    });
  }

  handleDelete(c: Customer): void {
    if (!confirm(`Supprimer le client ${c.name} et tous ses comptes ?`)) return;
    this.customerService.deleteCustomer(c.id!).subscribe({
      next: () => (this.customers = this.customers.filter(x => x.id !== c.id)),
      error: err => (this.errorMessage = err.error?.message ?? err.message)
    });
  }

  handleAccounts(c: Customer): void {
    this.router.navigateByUrl(`/admin/customer-accounts/${c.id}`, { state: c });
  }
}

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { CustomerService } from '../services/customer.service';
import { AccountService } from '../services/account.service';
import { AuthService } from '../services/auth.service';
import { Customer } from '../model/customer.model';
import { BankAccount } from '../model/account.model';

@Component({
  selector: 'app-customer-accounts',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DatePipe, DecimalPipe],
  templateUrl: './customer-accounts.component.html',
  styleUrl: './customer-accounts.component.css'
})
export class CustomerAccountsComponent implements OnInit {
  customerId!: number;
  customer?: Customer;
  accounts: BankAccount[] = [];
  newAccountForm: FormGroup;
  errorMessage = '';

  constructor(private route: ActivatedRoute, private customerService: CustomerService,
              private accountService: AccountService, public authService: AuthService, fb: FormBuilder) {
    this.newAccountForm = fb.group({
      type: fb.control('CURRENT'),
      initialBalance: fb.control(0, [Validators.required, Validators.min(0)]),
      overDraft: fb.control(5000),
      interestRate: fb.control(3.5)
    });
  }

  ngOnInit(): void {
    this.customerId = Number(this.route.snapshot.params['id']);
    this.customerService.getCustomer(this.customerId).subscribe(c => (this.customer = c));
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.customerService.customerAccounts(this.customerId).subscribe(a => (this.accounts = a));
  }

  createAccount(): void {
    const v = this.newAccountForm.value;
    this.accountService.newAccount(this.customerId, v.type, v.initialBalance, v.overDraft, v.interestRate).subscribe({
      next: () => this.loadAccounts(),
      error: err => (this.errorMessage = err.error?.message ?? err.message)
    });
  }

  changeStatus(account: BankAccount, status: string): void {
    this.accountService.updateStatus(account.id, status).subscribe(a => (account.status = a.status));
  }
}

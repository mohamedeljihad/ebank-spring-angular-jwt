import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
import { AccountService } from '../services/account.service';
import { AuthService } from '../services/auth.service';
import { AccountDetails } from '../model/account.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe, DecimalPipe],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css'
})
export class AccountsComponent implements OnInit {
  accountFormGroup: FormGroup;
  operationFormGroup: FormGroup;
  currentPage = 0;
  pageSize = 5;
  account?: AccountDetails;
  errorMessage = '';
  operationMessage = '';

  constructor(private fb: FormBuilder, private accountService: AccountService,
              public authService: AuthService, private route: ActivatedRoute) {
    this.accountFormGroup = this.fb.group({ accountId: this.fb.control('') });
    this.operationFormGroup = this.fb.group({
      operationType: this.fb.control('DEBIT'),
      amount: this.fb.control(0, [Validators.required, Validators.min(1)]),
      description: this.fb.control(''),
      accountDestination: this.fb.control('')
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.accountFormGroup.patchValue({ accountId: id });
      this.handleSearchAccount();
    }
  }

  handleSearchAccount(): void {
    const accountId = this.accountFormGroup.value.accountId?.trim();
    if (!accountId) return;
    this.errorMessage = '';
    this.accountService.getAccount(accountId, this.currentPage, this.pageSize).subscribe({
      next: data => (this.account = data),
      error: err => {
        this.account = undefined;
        this.errorMessage = err.error?.message ?? err.message;
      }
    });
  }

  gotoPage(page: number): void {
    this.currentPage = page;
    this.handleSearchAccount();
  }

  pages(): number[] {
    return Array.from({ length: this.account?.totalPages ?? 0 }, (_, i) => i);
  }

  handleAccountOperation(): void {
    const accountId = this.accountFormGroup.value.accountId;
    const { operationType, amount, description, accountDestination } = this.operationFormGroup.value;
    const done = {
      next: () => {
        this.operationMessage = `Opération ${operationType} de ${amount} effectuée`;
        this.operationFormGroup.reset({ operationType, amount: 0, description: '', accountDestination: '' });
        this.handleSearchAccount();
      },
      error: (err: any) => (this.operationMessage = 'Erreur : ' + (err.error?.message ?? err.message))
    };
    if (operationType === 'DEBIT') this.accountService.debit(accountId, amount, description).subscribe(done);
    else if (operationType === 'CREDIT') this.accountService.credit(accountId, amount, description).subscribe(done);
    else this.accountService.transfer(accountId, accountDestination, amount, description).subscribe(done);
  }
}

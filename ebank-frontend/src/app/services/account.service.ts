import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AccountDetails, BankAccount } from '../model/account.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private host = environment.backendHost;

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.host}/accounts`);
  }

  getAccount(accountId: string, page: number, size: number): Observable<AccountDetails> {
    return this.http.get<AccountDetails>(`${this.host}/accounts/${accountId}/pageOperations?page=${page}&size=${size}`);
  }

  newAccount(customerId: number, type: string, initialBalance: number, overDraft: number, interestRate: number): Observable<BankAccount> {
    return this.http.post<BankAccount>(`${this.host}/accounts`, { customerId, type, initialBalance, overDraft, interestRate });
  }

  updateStatus(accountId: string, status: string): Observable<BankAccount> {
    return this.http.patch<BankAccount>(`${this.host}/accounts/${accountId}/status?status=${status}`, {});
  }

  debit(accountId: string, amount: number, description: string): Observable<any> {
    return this.http.post(`${this.host}/accounts/debit`, { accountId, amount, description });
  }

  credit(accountId: string, amount: number, description: string): Observable<any> {
    return this.http.post(`${this.host}/accounts/credit`, { accountId, amount, description });
  }

  transfer(accountSource: string, accountDestination: string, amount: number, description: string): Observable<any> {
    return this.http.post(`${this.host}/accounts/transfer`, { accountSource, accountDestination, amount, description });
  }
}

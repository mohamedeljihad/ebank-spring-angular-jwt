import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Customer } from '../model/customer.model';
import { BankAccount } from '../model/account.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private host = environment.backendHost;

  constructor(private http: HttpClient) {}

  getCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.host}/customers`);
  }

  searchCustomers(keyword: string): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.host}/customers/search?keyword=${encodeURIComponent(keyword)}`);
  }

  getCustomer(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.host}/customers/${id}`);
  }

  saveCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(`${this.host}/customers`, customer);
  }

  updateCustomer(customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.host}/customers/${customer.id}`, customer);
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.host}/customers/${id}`);
  }

  customerAccounts(id: number): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.host}/customers/${id}/accounts`);
  }
}

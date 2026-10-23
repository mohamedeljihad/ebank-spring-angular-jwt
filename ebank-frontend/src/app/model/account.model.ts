import { Customer } from './customer.model';

export interface BankAccount {
  type: 'CurrentAccount' | 'SavingAccount';
  id: string;
  balance: number;
  createdAt: string;
  status: 'CREATED' | 'ACTIVATED' | 'SUSPENDED';
  createdBy?: string;
  customerDTO: Customer;
  overDraft?: number;
  interestRate?: number;
}

export interface AccountOperation {
  id: number;
  operationDate: string;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  description: string;
  performedBy?: string;
}

export interface AccountDetails {
  accountId: string;
  balance: number;
  type: string;
  customerName: string;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDTOS: AccountOperation[];
}

export interface Dashboard {
  totalCustomers: number;
  totalAccounts: number;
  totalOperations: number;
  totalBalance: number;
  accountsByType: Record<string, number>;
  accountsByStatus: Record<string, number>;
  operationsAmountByType: Record<string, number>;
  operationsByMonth: Record<string, Record<string, number>>;
  topCustomersBalance: Record<string, number>;
}

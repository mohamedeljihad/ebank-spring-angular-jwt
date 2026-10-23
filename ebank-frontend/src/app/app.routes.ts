import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AdminComponent } from './admin/admin.component';
import { CustomersComponent } from './customers/customers.component';
import { NewCustomerComponent } from './new-customer/new-customer.component';
import { EditCustomerComponent } from './edit-customer/edit-customer.component';
import { CustomerAccountsComponent } from './customer-accounts/customer-accounts.component';
import { AccountsComponent } from './accounts/accounts.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ChatbotComponent } from './chatbot/chatbot.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { UsersComponent } from './users/users.component';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';
import { authenticationGuard } from './guards/authentication.guard';
import { authorizationGuard } from './guards/authorization.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'admin', component: AdminComponent, canActivate: [authenticationGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'customers', component: CustomersComponent },
      { path: 'new-customer', component: NewCustomerComponent, canActivate: [authorizationGuard], data: { role: 'ADMIN' } },
      { path: 'edit-customer/:id', component: EditCustomerComponent, canActivate: [authorizationGuard], data: { role: 'ADMIN' } },
      { path: 'customer-accounts/:id', component: CustomerAccountsComponent },
      { path: 'accounts', component: AccountsComponent },
      { path: 'accounts/:id', component: AccountsComponent },
      { path: 'chatbot', component: ChatbotComponent },
      { path: 'change-password', component: ChangePasswordComponent },
      { path: 'users', component: UsersComponent, canActivate: [authorizationGuard], data: { role: 'ADMIN' } },
      { path: 'notAuthorized', component: NotAuthorizedComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

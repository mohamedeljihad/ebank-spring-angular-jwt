import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-edit-customer',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './edit-customer.component.html',
  styleUrl: './edit-customer.component.css'
})
export class EditCustomerComponent implements OnInit {
  form?: FormGroup;
  errorMessage = '';

  constructor(private route: ActivatedRoute, private fb: FormBuilder,
              private customerService: CustomerService, private router: Router) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.params['id']);
    this.customerService.getCustomer(id).subscribe({
      next: c => (this.form = this.fb.group({
        id: this.fb.control(c.id),
        name: this.fb.control(c.name, [Validators.required, Validators.minLength(3)]),
        email: this.fb.control(c.email, [Validators.required, Validators.email])
      })),
      error: err => (this.errorMessage = err.error?.message ?? err.message)
    });
  }

  update(): void {
    this.customerService.updateCustomer(this.form!.value).subscribe({
      next: () => this.router.navigateByUrl('/admin/customers'),
      error: err => (this.errorMessage = err.error?.message ?? err.message)
    });
  }
}

import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';

function samePasswords(group: AbstractControl): ValidationErrors | null {
  return group.get('newPassword')?.value === group.get('confirmPassword')?.value ? null : { mismatch: true };
}

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  form: FormGroup;
  message = '';
  error = '';

  constructor(fb: FormBuilder, public authService: AuthService) {
    this.form = fb.group({
      currentPassword: fb.control('', Validators.required),
      newPassword: fb.control('', [Validators.required, Validators.minLength(4)]),
      confirmPassword: fb.control('', Validators.required)
    }, { validators: samePasswords });
  }

  submit(): void {
    const { currentPassword, newPassword } = this.form.value;
    this.message = this.error = '';
    this.authService.changePassword(currentPassword, newPassword).subscribe({
      next: r => { this.message = r.message; this.form.reset(); },
      error: err => (this.error = err.error?.message ?? err.message)
    });
  }
}

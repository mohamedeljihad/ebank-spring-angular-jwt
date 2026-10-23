import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppUser, UserService } from '../services/user.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  users: AppUser[] = [];
  form: FormGroup;
  error = '';

  constructor(private userService: UserService, fb: FormBuilder) {
    this.form = fb.group({
      username: fb.control('', Validators.required),
      password: fb.control('', [Validators.required, Validators.minLength(4)]),
      email: fb.control('', Validators.email),
      role: fb.control('USER')
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.userService.getUsers().subscribe(u => (this.users = u));
  }

  addUser(): void {
    const { username, password, email, role } = this.form.value;
    this.userService.addUser(username, password, email, role).subscribe({
      next: () => { this.form.reset({ role: 'USER' }); this.load(); },
      error: err => (this.error = err.error?.message ?? err.message)
    });
  }

  rolesOf(u: AppUser): string {
    return u.roles.map(r => r.role).join(', ');
  }
}

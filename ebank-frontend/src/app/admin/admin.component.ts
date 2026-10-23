import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  template: `<app-navbar></app-navbar><div class="container-fluid px-4 mt-3"><router-outlet></router-outlet></div>`
})
export class AdminComponent {}

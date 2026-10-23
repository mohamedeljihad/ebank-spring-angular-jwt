import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AppUser { username: string; email: string; roles: { role: string }[]; }

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  getUsers(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(`${environment.backendHost}/auth/users`);
  }

  addUser(username: string, password: string, email: string, role: string): Observable<AppUser> {
    const params = new HttpParams().set('username', username).set('password', password).set('email', email).set('role', role);
    return this.http.post<AppUser>(`${environment.backendHost}/auth/users`, null, { params });
  }
}

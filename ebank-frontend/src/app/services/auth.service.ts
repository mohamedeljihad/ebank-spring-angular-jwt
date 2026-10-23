import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface JwtPayload { sub: string; scope: string; exp: number; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  isAuthenticated = false;
  roles: string[] = [];
  username: string | undefined;
  accessToken!: string;

  constructor(private http: HttpClient, private router: Router) {
    this.loadJwtTokenFromLocalStorage();   // avant la première navigation (guards)
  }

  public login(username: string, password: string): Observable<any> {
    const options = { headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded') };
    const params = new HttpParams().set('username', username).set('password', password);
    return this.http.post(`${environment.backendHost}/auth/login`, params, options);
  }

  loadProfile(data: any): void {
    this.isAuthenticated = true;
    this.accessToken = data['access-token'];
    const decoded = jwtDecode<JwtPayload>(this.accessToken);
    this.username = decoded.sub;
    this.roles = decoded.scope.split(' ');
    window.localStorage.setItem('jwt-token', this.accessToken);
  }

  logout(): void {
    this.isAuthenticated = false;
    this.accessToken = '';
    this.username = undefined;
    this.roles = [];
    window.localStorage.removeItem('jwt-token');
    this.router.navigateByUrl('/login');
  }

  /** Restaure la session au rechargement de la page si le jeton n'a pas expiré. */
  loadJwtTokenFromLocalStorage(): void {
    const token = window.localStorage.getItem('jwt-token');
    if (!token) return;
    const decoded = jwtDecode<JwtPayload>(token);
    if (decoded.exp * 1000 < Date.now()) {
      window.localStorage.removeItem('jwt-token');
      return;
    }
    this.loadProfile({ 'access-token': token });
  }

  hasRole(role: string): boolean {
    return this.roles.includes(role);
  }

  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    return this.http.post(`${environment.backendHost}/auth/change-password`, { currentPassword, newPassword });
  }
}

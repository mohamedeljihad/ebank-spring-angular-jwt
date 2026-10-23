import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/** Ajoute le JWT à toutes les requêtes (sauf /auth/login) et déconnecte l'utilisateur sur 401. */
export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const request = req.url.includes('/auth/login') || !auth.accessToken
    ? req
    : req.clone({ setHeaders: { Authorization: `Bearer ${auth.accessToken}` } });
  return next(request).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) auth.logout();
      return throwError(() => err);
    })
  );
};

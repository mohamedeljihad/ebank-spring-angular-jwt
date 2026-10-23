import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authorizationGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  return auth.hasRole(route.data['role']) ? true : inject(Router).createUrlTree(['/admin/notAuthorized']);
};

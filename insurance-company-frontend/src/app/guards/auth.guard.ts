import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const isLoggedIn = auth.isLoggedIn();
  const userRole = auth.getRole();
  const allowedRoles = route.data['roles'] as string[];

  if (!isLoggedIn) {
    router.navigate(['/login']);
    return false;
  }

  if (allowedRoles && !allowedRoles.includes(userRole || '')) {
    router.navigate(['/unauthorized']);
    alert("Vaš zahtjev je označen kao sumnjiv. Sesija je zaustavljena.");
    auth.logout();
    router.navigate(['/login']);
    return false;
  }

  return true;
};

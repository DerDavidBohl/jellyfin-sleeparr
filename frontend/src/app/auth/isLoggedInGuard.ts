import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from './auth.service';

export const isLoggedInGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  if (!authService.isLoggedIn()) {
    router.navigate(
      ['/login'],
      {queryParams: {returnUrl: state.url}}
    ).then();
    return false;
  }

  return true;
};

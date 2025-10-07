import {HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {catchError, Observable} from "rxjs";
import { AuthService } from "./auth.service";
import {inject} from '@angular/core';

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {

  const authService: AuthService = inject(AuthService);

  if(!authService.isLoggedIn() || req.url.startsWith('/api/login')) {
    return next(req);
  }

  const clone = req.clone({
    setHeaders: {Authorization: 'Bearer ' + authService.getToken()}
  });

  return next(clone).pipe(
    catchError((err) =>
      {
        if(err.status === 401) {
          authService.logout();
        }
        throw err;
      }
    )
  );
}

import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthResponse} from './auth-request';
import {map, tap} from 'rxjs';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

const jwtLocalStorageId: string = 'jwt';
const expiresAtStorageId = 'expires_at';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private _snackBar = inject(MatSnackBar);

  constructor(private http: HttpClient, private router: Router) {

  }

  login(username:string, password:string ) {
    return this.http.post<AuthResponse>('/api/v1/login', {username, password})
      .pipe(
        map(response => ({
          ...response,
          expirationDate: new Date(response.expirationDate)
        } as AuthResponse)),
        tap(this.setSession)
      );
  }

  private setSession(authResponse: AuthResponse) {
    localStorage.setItem(jwtLocalStorageId, authResponse.jwt);
    localStorage.setItem(expiresAtStorageId, authResponse.expirationDate.toISOString());
  }

  logout() {
    localStorage.removeItem(jwtLocalStorageId);
    localStorage.removeItem(expiresAtStorageId);

    this._snackBar.open('Logged out', undefined, {duration: 2000});
    this.router.navigate(['/login']).then();

  }

  public isLoggedIn(): boolean {

    const expiration = this.getExpiration();
    return !!expiration && !!this.getToken() && expiration.getTime() > new Date().getTime();
  }

  getExpiration(): Date | null {
    const expiration = localStorage.getItem(expiresAtStorageId);
    return expiration ? new Date(expiration) : null;
  }

  getToken() {
    return localStorage.getItem(jwtLocalStorageId);
  }
}

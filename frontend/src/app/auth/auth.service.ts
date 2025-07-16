import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthResponse} from './auth-request';
import {map, tap} from 'rxjs';

const jwtLocalStorageId: string = 'jwt';
const expiresAtStorageId = 'expires_at';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {

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

    console.log(authResponse);
    console.log(jwtLocalStorageId);
    console.log(expiresAtStorageId);
    localStorage.setItem(jwtLocalStorageId, authResponse.jwt);
    localStorage.setItem(expiresAtStorageId, authResponse.expirationDate.toISOString());
  }

  logout() {
    localStorage.removeItem(jwtLocalStorageId);
    localStorage.removeItem(expiresAtStorageId);
  }

  public isLoggedIn(): boolean {

    const expiration = this.getExpiration();
    const isLoggedIn = !!expiration && !!this.getToken() && expiration.getTime() > new Date().getTime();
    console.log(isLoggedIn);
    return isLoggedIn;
  }

  isLoggedOut() {
    return !this.isLoggedIn();
  }

  getExpiration(): Date | null {
    const expiration = localStorage.getItem(expiresAtStorageId);
    return expiration ? new Date(expiration) : null;
  }

  getToken() {
    return localStorage.getItem(jwtLocalStorageId);
  }
}

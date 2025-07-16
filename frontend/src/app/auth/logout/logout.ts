import { Component } from '@angular/core';
import {MatCard} from '@angular/material/card';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-logout',
  imports: [
    MatCard
  ],
  templateUrl: './logout.html',
  styleUrl: './logout.css'
})
export class Logout {
  get loggedIn(): boolean {
    return this.authService.isLoggedIn()
  }

  constructor(private authService: AuthService) {
    authService.logout();
  }

}

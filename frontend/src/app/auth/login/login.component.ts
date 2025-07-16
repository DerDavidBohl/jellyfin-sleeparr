import {Component} from '@angular/core';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatCard} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    MatFormField,
    MatLabel,
    MatCard,
    MatButton,
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username: string = '';
  password: string = '';


  constructor(private authService: AuthService, private router: Router) {
    if(this.authService.isLoggedIn()) {
      this.router.navigate(['configuration-panel']).then();
    }
  }

  login() {
    this.authService.login(this.username, this.password).subscribe((res) => {
      console.log(res);
      this.router.navigate(['configuration-panel']).then();
    });
  }
}

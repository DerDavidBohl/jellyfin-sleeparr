import {Component, inject} from '@angular/core';
import {ApiService} from "./api.service";
import {Me} from "./models/me";
import {Observable, switchMap} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {MatCard} from "@angular/material/card";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatIcon} from "@angular/material/icon";
import {AutoPauseConfiguration} from "./models/autoPauseConfiguration";
import {FormsModule} from "@angular/forms";
import {MatCheckbox} from "@angular/material/checkbox";
import {DurationPickerComponent} from './duration-picker-component/duration-picker-component';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AuthService} from '../auth/auth.service';

@Component({
  selector: 'app-configuration-panel',
  imports: [
    AsyncPipe,
    MatCard,
    MatIconButton,
    MatIcon,
    MatMenu,
    MatMenuTrigger,
    MatMenuItem,
    FormsModule,
    MatCheckbox,
    DurationPickerComponent,
    MatFormField,
    MatInput,
    MatLabel,
    MatFormField,
    MatButton,
  ],
  templateUrl: './configuration-panel.component.html',
  styleUrl: './configuration-panel.component.css'
})
export class ConfigurationPanelComponent {

  $me: Observable<Me>;
  $autoPauseConfiguration: Observable<AutoPauseConfiguration>;

  private _snackBar = inject(MatSnackBar);

  constructor(private apiService: ApiService, private authService: AuthService) {
    this.$me = this.apiService.getMe();
    this.$autoPauseConfiguration = this.$me.pipe(
        switchMap(me => this.apiService.getAutoPauseConfiguration(me.id))
    );
  }

  protected readonly console = console;

  saveConfiguration(config: AutoPauseConfiguration) {
    this.apiService.putAutoPauseConfiguration(config.userId, config)
      .subscribe(() => this._snackBar.open('Configuration saved', undefined, {duration: 2000}));
  }

  logout() {
    this.authService.logout();
    this._snackBar.open('Logged out', undefined, {duration: 2000});
  }
}

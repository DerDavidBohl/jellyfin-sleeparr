import {Routes} from '@angular/router';
import {isLoggedInGuard} from './auth/isLoggedInGuard';
import {LoginComponent} from './auth/login/login.component';
import {configurationPanelRoutes} from './configuration-panel/configuration-panel.routes';
import {Logout} from './auth/logout/logout';

export const routes: Routes = [
  { path: 'login', component: LoginComponent},
  { path: 'logout', component: Logout, canActivate: [isLoggedInGuard]},
  { path: 'configuration-panel', canActivate: [isLoggedInGuard], children: configurationPanelRoutes},
  { path: '**', redirectTo: '/login'},
];


import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Me} from './models/me';
import {AutoPauseConfiguration} from './models/autoPauseConfiguration';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {
  }

  getMe(): Observable<Me> {
    return this.http.get<Me>('/api/v1/me');
  }

  getAutoPauseConfiguration(userId: string): Observable<AutoPauseConfiguration> {
    return this.http.get<AutoPauseConfiguration>('/api/v1/users/' + userId + '/auto-pause-configuration');
  }

  putAutoPauseConfiguration(userId: string, autoPauseConfiguration: AutoPauseConfiguration): Observable<AutoPauseConfiguration> {
    return this.http.put<AutoPauseConfiguration>('/api/v1/users/' + userId + '/auto-pause-configuration', autoPauseConfiguration);
  }

}

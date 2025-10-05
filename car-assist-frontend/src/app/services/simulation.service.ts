import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {env} from '../../env/env';

@Injectable({
  providedIn: 'root'
})
export class SimulationService {
  constructor(private http: HttpClient) {}

  startSimulation(type: string): Observable<string> {
    const url = `${env.api}/api/simulate/${type}`;
    return this.http.get(url, { responseType: 'text' });
  }
}

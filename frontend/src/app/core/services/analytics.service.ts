import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UserStats } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  constructor(private http: HttpClient) {}

  getStats() {
    return this.http.get<UserStats>(`${environment.apiUrl}/analytics/stats`);
  }
}

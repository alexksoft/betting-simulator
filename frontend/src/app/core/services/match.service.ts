import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Match, MatchRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class MatchService {
  private base = `${environment.apiUrl}/matches`;

  constructor(private http: HttpClient) {}

  getAll(status?: string) {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<Match[]>(`${this.base}/public`, { params });
  }

  getById(id: string) {
    return this.http.get<Match>(`${this.base}/public/${id}`);
  }

  create(req: MatchRequest) {
    return this.http.post<Match>(this.base, req);
  }

  settle(matchId: string, result: string) {
    return this.http.put<Match>(`${this.base}/${matchId}/settle`, { result });
  }
}

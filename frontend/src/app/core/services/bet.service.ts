import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Bet, BetRequest, PublicBet, MatchStats } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BetService {
  private base = `${environment.apiUrl}/bets`;

  constructor(private http: HttpClient) {}

  place(req: BetRequest) {
    return this.http.post<Bet>(this.base, req);
  }

  getMyBets() {
    return this.http.get<Bet[]>(this.base);
  }

  getMatchBets(matchId: string) {
    return this.http.get<Bet[]>(`${this.base}/match/${matchId}`);
  }

  getPublicMatchBets(matchId: string) {
    return this.http.get<PublicBet[]>(`${this.base}/public/match/${matchId}`);
  }

  getMatchStats(matchId: string) {
    return this.http.get<MatchStats>(`${this.base}/public/match/${matchId}/stats`);
  }
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatchService } from '../../core/services/match.service';
import { BetService } from '../../core/services/bet.service';
import { AuthService } from '../../core/services/auth.service';
import { Match, MatchRequest } from '../../core/models/models';

@Component({
  selector: 'app-matches',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './matches.component.html'
})
export class MatchesComponent implements OnInit {
  matches: Match[] = [];
  loading = true;
  statusFilter = '';
  showCreateForm = false;
  selectedMatch: Match | null = null;
  message = '';

  newMatch: MatchRequest = { sport: '', homeTeam: '', awayTeam: '', startTime: '', oddsHome: undefined, oddsDraw: undefined, oddsAway: undefined };
  betStake = 10;
  betType = 'HOME_WIN';
  customOdds: number | null = null;

  constructor(
    private matchService: MatchService,
    private betService: BetService,
    public auth: AuthService
  ) {}

  ngOnInit() { this.loadMatches(); }

  loadMatches() {
    this.loading = true;
    this.matchService.getAll(this.statusFilter || undefined).subscribe({
      next: (m) => { this.matches = m; this.loading = false; },
      error: () => this.loading = false
    });
  }

  createMatch() {
    const payload = {
      ...this.newMatch,
      oddsHome: this.newMatch.oddsHome ? Number(this.newMatch.oddsHome) : undefined,
      oddsDraw: this.newMatch.oddsDraw ? Number(this.newMatch.oddsDraw) : undefined,
      oddsAway: this.newMatch.oddsAway ? Number(this.newMatch.oddsAway) : undefined
    };
    this.matchService.create(payload).subscribe({
      next: (m) => {
        this.matches.unshift(m);
        this.showCreateForm = false;
        this.newMatch = { sport: '', homeTeam: '', awayTeam: '', startTime: '' };
        this.message = 'Match created!';
      },
      error: (e) => this.message = e.error?.error || 'Failed to create match'
    });
  }

  selectMatch(match: Match) {
    this.selectedMatch = this.selectedMatch?.matchId === match.matchId ? null : match;
    this.betType = 'HOME_WIN';
    this.betStake = 10;
    this.customOdds = null;
  }

  getOddsForType(match: Match): number {
    if (this.customOdds && this.customOdds > 1) return this.customOdds;
    if (this.betType === 'HOME_WIN') return match.oddsHome ?? 0;
    if (this.betType === 'DRAW') return match.oddsDraw ?? 0;
    return match.oddsAway ?? 0;
  }

  effectiveOdds(): number {
    if (!this.selectedMatch) return 0;
    return this.getOddsForType(this.selectedMatch);
  }

  placeBet() {
    if (!this.selectedMatch) return;
    const odds = this.effectiveOdds();
    this.betService.place({ matchId: this.selectedMatch.matchId, betType: this.betType, stake: this.betStake, odds }).subscribe({
      next: () => {
        this.message = `Bet placed! Potential win: £${(this.betStake * odds).toFixed(2)}`;
        this.selectedMatch = null;
        const user = this.auth.currentUser$.value;
        if (user) this.auth.updateBankroll(user.bankroll - this.betStake);
      },
      error: (e) => this.message = e.error?.error || 'Failed to place bet'
    });
  }
}

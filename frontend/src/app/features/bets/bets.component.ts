import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BetService } from '../../core/services/bet.service';
import { Bet } from '../../core/models/models';

@Component({
  selector: 'app-bets',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bets.component.html'
})
export class BetsComponent implements OnInit {
  bets: Bet[] = [];
  loading = true;

  constructor(private betService: BetService) {}

  ngOnInit() {
    this.betService.getMyBets().subscribe({
      next: (b) => { this.bets = b.sort((a, z) => z.placedAt.localeCompare(a.placedAt)); this.loading = false; },
      error: () => this.loading = false
    });
  }

  get totalStaked() { return this.bets.reduce((s, b) => s + b.stake, 0); }
  get totalPL() { return this.bets.filter(b => b.profitLoss != null).reduce((s, b) => s + (b.profitLoss ?? 0), 0); }

  getUkrainianTime(dateString: string): string {
    try {
      const date = new Date(dateString);
      return date.toLocaleString('uk-UA', {
        timeZone: 'Europe/Kiev',
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      return dateString;
    }
  }
}

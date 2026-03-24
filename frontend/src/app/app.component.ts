import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar" *ngIf="auth.isLoggedIn()">
      <div class="nav-brand">🎯 BetSim</div>
      <div class="nav-links">
        <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
        <a routerLink="/matches" routerLinkActive="active">Matches</a>
        <a routerLink="/bets" routerLinkActive="active">My Bets</a>
      </div>
      <div class="nav-user">
        <span class="bankroll">💰 £{{ (auth.currentUser$ | async)?.bankroll | number:'1.2-2' }}</span>
        <span class="username">{{ (auth.currentUser$ | async)?.username }}</span>
        <button class="btn btn-sm" (click)="auth.logout()">Logout</button>
      </div>
    </nav>
    <main>
      <router-outlet />
    </main>
  `
})
export class AppComponent {
  constructor(public auth: AuthService) {}
}

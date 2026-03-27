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
      
      <!-- Desktop Navigation -->
      <div class="nav-links desktop-nav">
        <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
        <a routerLink="/matches" routerLinkActive="active">Matches</a>
        <a routerLink="/bets" routerLinkActive="active">My Bets</a>
      </div>
      
      <!-- Mobile Menu Button -->
      <button class="mobile-menu-btn" (click)="toggleMobileMenu()">
        <span class="hamburger-line" [class.active]="showMobileMenu"></span>
        <span class="hamburger-line" [class.active]="showMobileMenu"></span>
        <span class="hamburger-line" [class.active]="showMobileMenu"></span>
      </button>
      
      <div class="nav-user">
        <span class="bankroll">💰 £{{ (auth.currentUser$ | async)?.bankroll | number:'1.2-2' }}</span>
        <span class="username desktop-only">{{ (auth.currentUser$ | async)?.username }}</span>
        <button class="btn btn-sm" (click)="auth.logout()">Logout</button>
      </div>
    </nav>
    
    <!-- Mobile Navigation Popup -->
    <div class="mobile-nav-overlay" *ngIf="showMobileMenu" (click)="closeMobileMenu()">
      <div class="mobile-nav-popup" (click)="$event.stopPropagation()">
        <div class="mobile-nav-header">
          <span class="mobile-nav-title">Navigation</span>
          <button class="close-btn" (click)="closeMobileMenu()">&times;</button>
        </div>
        <div class="mobile-nav-links">
          <a routerLink="/dashboard" routerLinkActive="active" (click)="closeMobileMenu()">
            <span class="nav-icon">📊</span>
            Dashboard
          </a>
          <a routerLink="/matches" routerLinkActive="active" (click)="closeMobileMenu()">
            <span class="nav-icon">⚽</span>
            Matches
          </a>
          <a routerLink="/bets" routerLinkActive="active" (click)="closeMobileMenu()">
            <span class="nav-icon">🎯</span>
            My Bets
          </a>
        </div>
        <div class="mobile-nav-user">
          <div class="mobile-user-info">
            <span class="mobile-username">{{ (auth.currentUser$ | async)?.username }}</span>
            <span class="mobile-bankroll">💰 £{{ (auth.currentUser$ | async)?.bankroll | number:'1.2-2' }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <main>
      <router-outlet />
    </main>
  `
})
export class AppComponent {
  showMobileMenu = false;
  
  constructor(public auth: AuthService) {}
  
  toggleMobileMenu() {
    this.showMobileMenu = !this.showMobileMenu;
  }
  
  closeMobileMenu() {
    this.showMobileMenu = false;
  }
}

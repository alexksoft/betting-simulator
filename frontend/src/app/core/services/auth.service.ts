import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'bs_token';
  private readonly USER_KEY = 'bs_user';

  currentUser$ = new BehaviorSubject<AuthResponse | null>(this.storedUser());

  constructor(private http: HttpClient, private router: Router) {}

  register(email: string, username: string, password: string) {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, { email, username, password })
      .pipe(tap(res => this.storeSession(res)));
  }

  login(email: string, password: string) {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, { email, password })
      .pipe(tap(res => this.storeSession(res)));
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser$.next(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  updateBankroll(bankroll: number) {
    const user = this.currentUser$.value;
    if (user) {
      const updated = { ...user, bankroll };
      localStorage.setItem(this.USER_KEY, JSON.stringify(updated));
      this.currentUser$.next(updated);
    }
  }

  private storeSession(res: AuthResponse) {
    localStorage.setItem(this.TOKEN_KEY, res.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(res));
    this.currentUser$.next(res);
  }

  private storedUser(): AuthResponse | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? JSON.parse(raw) : null;
  }
}

export interface User {
  userId: string;
  username: string;
  email: string;
  bankroll: number;
}

export interface AuthResponse {
  token: string;
  userId: string;
  username: string;
  email: string;
  bankroll: number;
}

export interface Match {
  matchId: string;
  sport: string;
  homeTeam: string;
  awayTeam: string;
  startTime: string;
  status: 'UPCOMING' | 'LIVE' | 'FINISHED';
  result?: 'HOME_WIN' | 'DRAW' | 'AWAY_WIN';
  createdBy: string;
  oddsHome?: number;
  oddsDraw?: number;
  oddsAway?: number;
}

export interface Bet {
  betId: string;
  userId: string;
  matchId: string;
  betType: 'HOME_WIN' | 'DRAW' | 'AWAY_WIN';
  stake: number;
  odds: number;
  potentialWin: number;
  status: 'PENDING' | 'WON' | 'LOST' | 'VOID';
  profitLoss?: number;
  placedAt: string;
}

export interface BetRequest {
  matchId: string;
  betType: string;
  stake: number;
  odds: number;
}

export interface MatchRequest {
  sport: string;
  homeTeam: string;
  awayTeam: string;
  startTime: string;
  oddsHome?: number;
  oddsDraw?: number;
  oddsAway?: number;
}

export interface BankrollPoint {
  timestamp: string;
  balance: number;
  reason: string;
}

export interface UserStats {
  totalBets: number;
  wonBets: number;
  lostBets: number;
  pendingBets: number;
  winRate: number;
  totalStaked: number;
  totalReturns: number;
  profitLoss: number;
  roi: number;
  currentBankroll: number;
  bankrollHistory: BankrollPoint[];
}

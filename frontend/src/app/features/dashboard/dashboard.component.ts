import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BaseChartDirective, NgChartsModule } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { AnalyticsService } from '../../core/services/analytics.service';
import { AuthService } from '../../core/services/auth.service';
import { UserStats } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, NgChartsModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  stats: UserStats | null = null;
  loading = true;

  chartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Bankroll (£)', fill: true, tension: 0.4,
      borderColor: '#6366f1', backgroundColor: 'rgba(99,102,241,0.1)' }]
  };
  chartOptions: ChartConfiguration<'line'>['options'] = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: false } }
  };

  constructor(private analytics: AnalyticsService, public auth: AuthService) {}

  ngOnInit() {
    this.analytics.getStats().subscribe({
      next: (s) => {
        this.stats = s;
        this.auth.updateBankroll(s.currentBankroll);
        this.chartData = {
          labels: s.bankrollHistory.map(p => new Date(p.timestamp).toLocaleDateString()),
          datasets: [{ ...this.chartData.datasets[0], data: s.bankrollHistory.map(p => p.balance) }]
        };
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}

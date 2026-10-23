import { Component, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData } from 'chart.js';
import { DashboardService } from '../services/dashboard.service';
import { Dashboard } from '../model/account.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [BaseChartDirective, DecimalPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  stats?: Dashboard;
  errorMessage = '';

  monthlyData: ChartData<'bar'> = { labels: [], datasets: [] };
  typeData: ChartData<'pie'> = { labels: [], datasets: [] };
  statusData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  topData: ChartData<'bar'> = { labels: [], datasets: [] };
  barOptions: ChartConfiguration<'bar'>['options'] = { responsive: true, plugins: { legend: { position: 'top' } } };
  hBarOptions: ChartConfiguration<'bar'>['options'] = { responsive: true, indexAxis: 'y', plugins: { legend: { display: false } } };

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getDashboard().subscribe({
      next: d => {
        this.stats = d;
        const months = Object.keys(d.operationsByMonth);
        this.monthlyData = {
          labels: months,
          datasets: [
            { label: 'Crédits', data: months.map(m => d.operationsByMonth[m]['CREDIT'] ?? 0), backgroundColor: '#198754' },
            { label: 'Débits', data: months.map(m => d.operationsByMonth[m]['DEBIT'] ?? 0), backgroundColor: '#dc3545' }
          ]
        };
        this.typeData = {
          labels: Object.keys(d.accountsByType).map(t => (t === 'CurrentAccount' ? 'Courants' : 'Épargne')),
          datasets: [{ data: Object.values(d.accountsByType), backgroundColor: ['#0d6efd', '#ffc107'] }]
        };
        this.statusData = {
          labels: Object.keys(d.accountsByStatus),
          datasets: [{ data: Object.values(d.accountsByStatus), backgroundColor: ['#198754', '#6c757d', '#dc3545'] }]
        };
        this.topData = {
          labels: Object.keys(d.topCustomersBalance),
          datasets: [{ label: 'Solde total', data: Object.values(d.topCustomersBalance), backgroundColor: '#6610f2' }]
        };
      },
      error: err => (this.errorMessage = err.error?.message ?? err.message)
    });
  }
}

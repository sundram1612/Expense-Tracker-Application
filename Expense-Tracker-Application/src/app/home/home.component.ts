import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ExpenseService } from '../expense.service';
import { ToastService } from '../shared/notifications/toast.service';
import { DashboardDTO } from '../shared/models/dashboard.model';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { AddExpenseModalComponent } from '../shared/modals/add-expense-modal.component';
import { AddIncomeModalComponent } from '../shared/modals/add-income-modal.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, AddExpenseModalComponent, AddIncomeModalComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  dashboardData: DashboardDTO | null = null;
  loading = true;
  error = '';

  showAddExpense = false;
  showAddIncome = false;
  
  // Chart configs
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'right' },
    }
  };
  public pieChartData: ChartData<'pie', number[], string | string[]> = {
    labels: [],
    datasets: [ { data: [] } ]
  };
  public pieChartType: ChartType = 'pie';

  public lineChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: []
  };
  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    elements: {
      line: { tension: 0.5 }
    },
    scales: {
      y: { position: 'left' }
    },
    plugins: { legend: { display: true } }
  };
  public lineChartType: ChartType = 'line';

  // Heatmap configuration
  heatmapDays: { date: Date, level: number, title: string }[] = [];
  heatmapDaysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  constructor(private expenseService: ExpenseService, private router: Router, private toastService: ToastService) {}

  navigateToReports() {
    this.router.navigate(['/reports']);
  }

  ngOnInit() {
    this.fetchDashboardData();
  }

  fetchDashboardData() {
    this.expenseService.getDashboardData().subscribe({
      next: (data: DashboardDTO) => {
        this.dashboardData = data;
        this.processCharts(data);
        this.processHeatmap(data.heatmapData);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load dashboard data.';
        this.loading = false;
        this.toastService.error('Failed to load dashboard data. Please check your connection.', 'Dashboard Error');
        console.error(err);
      }
    });
  }

  processCharts(data: DashboardDTO) {
    if (data.expenseBreakdown && data.expenseBreakdown.length > 0) {
      this.pieChartData = {
        labels: data.expenseBreakdown.map(b => b.category),
        datasets: [{
          data: data.expenseBreakdown.map(b => b.amount),
          backgroundColor: ['#4285F4', '#EA4335', '#FBBC05', '#34A853', '#9C27B0']
        }]
      };
    }

    if (data.incomeVsExpense && data.incomeVsExpense.length > 0) {
      const reversed = [...data.incomeVsExpense].reverse();
      this.lineChartData = {
        labels: reversed.map(m => m.month),
        datasets: [
          {
            data: reversed.map(m => m.income),
            label: 'Income',
            backgroundColor: 'rgba(52, 168, 83, 0.2)',
            borderColor: '#34A853',
            pointBackgroundColor: '#34A853',
            fill: 'origin',
          },
          {
            data: reversed.map(m => m.expense),
            label: 'Expense',
            backgroundColor: 'rgba(234, 67, 53, 0.2)',
            borderColor: '#EA4335',
            pointBackgroundColor: '#EA4335',
            fill: 'origin',
          }
        ]
      };
    }
  }

  processHeatmap(heatmapData: any) {
    // Generate last 30 days
    const days = [];
    for (let i = 29; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const dateStr = d.toISOString().split('T')[0];
      const count = heatmapData ? (heatmapData[dateStr] || 0) : 0;
      let level = 0;
      if(count > 0 && count <= 2) level = 1;
      else if(count > 2 && count <= 5) level = 2;
      else if(count > 5) level = 3;
      
      days.push({
        date: d,
        title: `${dateStr}: ${count} transactions`,
        level: level
      });
    }
    this.heatmapDays = days;
  }
}
      
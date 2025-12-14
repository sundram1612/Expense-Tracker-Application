import { CommonModule, DecimalPipe } from '@angular/common';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Chart, registerables } from 'chart.js';
import { Expense, ExpenseService } from '../expense.service';
import { ThemeToggleComponent } from '../shared/theme-toggle/theme-toggle.component';
@Component({
  selector: 'app-pie-chart',
  standalone: true,
  imports: [CommonModule, RouterModule, DecimalPipe, ThemeToggleComponent],
  templateUrl: './pie-chart.component.html',
  styleUrl: './pie-chart.component.css'
})


export class PieChartComponent implements OnInit, AfterViewInit {
  expenses: Expense[] = [];
  totalExpenses = 0;
  categoryBreakdown: any[] = [];
  private pieChart: Chart | undefined;

  constructor(private expenseService: ExpenseService) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    this.loadExpenses();
  }

  ngAfterViewInit() { }

  private loadExpenses() {
    this.expenseService.getExpenses().subscribe({
      next: (data) => {
        this.expenses = data;
        this.totalExpenses = this.getTotalExpenses();
        this.updateCategoryBreakdown();
        this.initializeChart();
      },
      error: () => alert('Failed to load expenses for chart!')
    });
  }

  private getTotalExpenses(): number {
    return this.expenses.reduce((total, exp) => total + exp.amount, 0);
  }

  private updateCategoryBreakdown() {
    const categories = ['Food', 'Travel', 'Shopping', 'Utilities', 'Other'];
    this.categoryBreakdown = categories.map(category => {
      const categoryExpenses = this.expenses.filter(exp => exp.category === category);
      const total = categoryExpenses.reduce((sum, exp) => sum + exp.amount, 0);
      return { name: category, amount: total };
    }).filter(cat => cat.amount > 0);

    console.log("Expenses:", this.expenses);
    console.log("Category breakdown:", this.categoryBreakdown);
  }


  getCategoryColor(category: string): string {
    const colorMap: { [key: string]: string } = {
      'Food': '#ff6b6b',
      'Travel': '#4ecdc4',
      'Shopping': '#45b7d1',
      'Utilities': '#96ceb4',
      'Other': '#feca57'
    };
    return colorMap[category] || '#6c757d';
  }
  private initializeChart() {
    if (this.pieChart) {
      this.pieChart.destroy();
    }

    const ctx = document.getElementById('pieChart') as HTMLCanvasElement;

    const total = this.categoryBreakdown.reduce((a, b) => a + b.amount, 0);
    const minPercent = 0.02;
    const adjustedData = this.categoryBreakdown.map(cat => {
      const percent = cat.amount / total;
      return percent < minPercent ? total * minPercent : cat.amount;
    });

    this.pieChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: this.categoryBreakdown.map(cat => cat.name),
        datasets: [{
          data: adjustedData,
          backgroundColor: this.categoryBreakdown.map(cat => this.getCategoryColor(cat.name)),
          borderColor: '#ffffff',
          borderWidth: 2,
          hoverOffset: 12,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '55%',
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (context) => {
                const label = context.label || '';
                const value = this.categoryBreakdown[context.dataIndex].amount;
                const percent = ((value / total) * 100).toFixed(1);
                return `${label}: ₹${value.toFixed(2)} (${percent}%)`;
              }
            }
          }
        }
      }
    });
  }

}

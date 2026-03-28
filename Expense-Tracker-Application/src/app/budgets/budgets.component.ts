import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../expense.service';
import { DashboardDTO } from '../shared/models/dashboard.model';
import { AddBudgetModalComponent } from '../shared/modals/add-budget-modal.component';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, AddBudgetModalComponent],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css'
})
export class BudgetsComponent implements OnInit {
  dashboardData: any = null;
  loading = true;
  totalBudget = 0;
  showAddBudget = false;

  constructor(private expenseService: ExpenseService) {}

  ngOnInit() {
    this.fetchData();
  }

  fetchData() {
    this.loading = true;
    this.expenseService.getDashboardData().subscribe({
      next: (data: DashboardDTO) => {
        this.dashboardData = data;
        this.totalBudget = (data.budgetRemaining || 0) + (data.thisMonthExpense || 0);
        this.loading = false;
      },
      error: (err: any) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  getCategoryPercentage(amount: number): number {
    if (!this.totalBudget) return 0;
    return (amount / this.totalBudget) * 100;
  }
}

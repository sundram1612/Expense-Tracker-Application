import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../expense.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css'
})
export class TransactionsComponent implements OnInit {
  transactions: any[] = [];
  filteredTransactions: any[] = [];
  loading = true;
  error = '';
  
  activeFilter = 'All'; 
  searchQuery = '';

  constructor(private expenseService: ExpenseService) {}

  ngOnInit() {
    this.expenseService.getDashboardData().subscribe({
      next: (data: any) => {       
        this.fetchTransactions();
      },
      error: () => this.fetchTransactions()
    });
  }

  fetchTransactions() {
    this.expenseService.getExpenses().subscribe({
      next: (data: any) => {
        this.transactions = data.sort((a: any, b: any) => new Date(b.date).getTime() - new Date(a.date).getTime());
        this.applyFilters();
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load transactions';
        this.loading = false;
        console.error(err);
      }
    });
  }

  setFilter(filter: string) {
    this.activeFilter = filter;
    this.applyFilters();
  }

  applyFilters() {
    this.filteredTransactions = this.transactions.filter(t => {
      if (this.activeFilter === 'Income' && t.type !== 'INCOME') return false;
      if (this.activeFilter === 'Expense' && t.type !== 'EXPENSE') return false;
      
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        const descMatch = t.description?.toLowerCase().includes(query) || false;
        const catMatch = t.category?.toLowerCase().includes(query) || false;
        if (!descMatch && !catMatch) return false;
      }
      
      return true;
    });
  }

  onSearch(event: any) {
    this.searchQuery = event.target.value;
    this.applyFilters();
  }

  getStatus(dateStr: string): string {
    const txDate = new Date(dateStr);
    const today = new Date();
    today.setHours(0,0,0,0);
    return txDate > today ? 'Pending' : 'Completed';
  }
}

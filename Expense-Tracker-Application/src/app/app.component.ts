import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { ExpenseFormComponent } from './expense-form/expense-form.component';
import { ExpenseListComponent } from './expense-list/expense-list.component';
import { ExpenseSummaryComponent } from './expense-summary/expense-summary.component';
import { ExpenseService, Expense } from './expense.service';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule,
    RouterModule,
    HttpClientModule,
    HeaderComponent
],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  // expenses: Expense[] = [];
  // totalExpenses = 0;
  // categoryBreakdown: any[] = [];
  // title: any;

  // constructor(private expenseService: ExpenseService){
    
  // }

  // ngOnInit(): void {
  //   if(typeof window !== 'undefined'){
  //     this.loadExpenses();
  //   }    
  // }

  // onExpenseAdded(expense: Expense) {
  //   this.expenseService.addExpense(expense).subscribe({
  //     next: () => this.loadExpenses(),
  //     error: (err) => {console.error('Add expense error', err); alert('Failed to add expense!')}
  //   });
  // }

  
  // onExpenseDeleted(id: string) {
  //   this.expenseService.deleteExpense(id).subscribe({
  //     next: () => this.loadExpenses(),
  //     error: () => alert('Failed to delete expense!')
  //   });
  // }

  
  // private loadExpenses() {
  //   this.expenseService.getExpenses().subscribe({
  //     next: (data) => {
  //       // this.expenses = data.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  //       this.expenses = Array.isArray(data) ? data.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()) : [];
  //       this.totalExpenses = this.getTotalExpenses();
  //       this.updateCategoryBreakdown();
  //     },
  //     error: () => alert('Failed to load expenses!')
  //   });
  // }

  // private getTotalExpenses(): number {
  //   return this.expenses.reduce((total, exp) => total + exp.amount, 0);
  // }

  // private updateCategoryBreakdown() {
  //   const categories = ['Food', 'Travel', 'Shopping', 'Utilities', 'Other'];
  //   this.categoryBreakdown = categories.map(category => {
  //     const categoryExpenses = this.expenses.filter(exp => exp.category === category);
  //     const total = categoryExpenses.reduce((sum, exp) => sum + exp.amount, 0);
  //     return { name: category, amount: total };
  //   }).filter(cat => cat.amount > 0);
  // }
}

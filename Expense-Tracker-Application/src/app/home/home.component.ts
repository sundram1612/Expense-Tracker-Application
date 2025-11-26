import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { ExpenseFormComponent } from '../expense-form/expense-form.component';
import { ExpenseListComponent } from '../expense-list/expense-list.component';
import { ExpenseSummaryComponent } from '../expense-summary/expense-summary.component';
import { Expense, ExpenseService } from '../expense.service';

@Component({
   selector: 'app-root',
   standalone: true,
   imports: [
    CommonModule,
    RouterModule,
    HttpClientModule,
    ExpenseFormComponent,
    ExpenseListComponent,
    ExpenseSummaryComponent
],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  expenses: Expense[] = [];
    totalExpenses = 0;
    categoryBreakdown: any[] = [];
    // title: any;
  
    constructor(private expenseService: ExpenseService) {
      // this.loadExpenses();
    }
  
    ngOnInit(): void {
      if(typeof window !== 'undefined'){
        this.loadExpenses();
      }
    }

    onExpenseAdded(expense: Expense) {

      if(!expense.date){
        expense.date = new Date().toISOString().substring(0,10);
      }

      this.expenseService.addExpense(expense).subscribe({
        next: () =>{
          // console.log("Expense Added: ", res)
          alert("Expense added Successfully!");
           this.loadExpenses();
          },
        error: (err) => {
          console.error(err);
          alert('Failed to add expense: '+ err.message);
        }
      });
    }
  
    
    onExpenseDeleted(id: string) {
      this.expenseService.deleteExpense(id).subscribe({
        next: () => {
          // alert("Expense deleted Successfully!")
          // this.loadExpenses();
          this.expenses = this.expenses.filter(exp => exp.id !== id);
          this.totalExpenses = this.getTotalExpenses();
          this.updateCategoryBreakdown();
          // this.loadExpenses();
        },
        error: (err) => {
          console.error(err);
          alert('Failed to delete expense: '+err.message);
        }
      });
    }
  
    
    private loadExpenses() {
      this.expenseService.getExpenses().subscribe({
        next: (data) => {
          console.log("Fetched expense: ",data);
          if(!Array.isArray(data)) data = [];

          data = data.map(e => {
            e.date = e.date?.substring(0,10);
            return e;
          });

          this.expenses = data.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
          this.totalExpenses = this.getTotalExpenses();
          this.updateCategoryBreakdown();
        },
        error: (err) => alert('Failed to load expenses: '+ err.message)
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
    }
}

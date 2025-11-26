import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-expense-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './expense-form.component.html',
  styleUrl: './expense-form.component.css'
})
export class ExpenseFormComponent {
  @Output() expenseAdded = new EventEmitter<any>();

  newExpense = {
    description: '',
    amount: null,
    category: '',
    date: new Date().toISOString().split('T')[0]
  };

  onSubmit() {
    if (this.newExpense.description && this.newExpense.amount && this.newExpense.category && this.newExpense.date) {
      this.expenseAdded.emit({ ...this.newExpense });
      this.resetForm();
    } else {
      alert('Please fill all fields');
    }
  }

  private resetForm() {
    this.newExpense = {
      description: '',
      amount: null,
      category: '',
      date: new Date().toISOString().split('T')[0]
    };
  }
}

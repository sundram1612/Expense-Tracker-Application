import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Expense } from '../expense.service';

@Component({
  selector: 'app-expense-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.css'
})
export class ExpenseListComponent {
  @Input() expenses: Expense[] = [];
  @Output() expenseDeleted = new EventEmitter<string>();

  getCategoryColor(category: string): string {
    const colors: { [key: string]: string } = {
      'Food': '#ff6b6b',
      'Travel': '#4ecdc4',
      'Shopping': '#45b7d1',
      'Utilities': '#96ceb4',
      'Other': '#feca57'
    };
    return colors[category] || '#667eea';
  }

  onDelete(id?: string) {
    if (id) {
      this.expenseDeleted.emit(id);
    }
  }
}

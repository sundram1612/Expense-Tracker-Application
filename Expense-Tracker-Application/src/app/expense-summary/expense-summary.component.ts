import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-expense-summary',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './expense-summary.component.html',
  styleUrl: './expense-summary.component.css'
})
export class ExpenseSummaryComponent {
  @Input() totalExpenses: number = 0;
  @Input() categoryBreakdown: any[] = [];

  getCategoryColor(category: string): string {
    const colors: {[key: string]: string} = {
      'Food': '#ff6b6b',
      'Travel': '#4ecdc4',
      'Shopping': '#45b7d1',
      'Utilities': '#96ceb4',
      'Other': '#feca57'
    };
    return colors[category]
  }


}



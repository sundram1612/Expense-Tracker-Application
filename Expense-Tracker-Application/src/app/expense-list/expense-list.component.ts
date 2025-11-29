import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Expense, ExpenseService } from '../expense.service';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-expense-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.css'
})
export class ExpenseListComponent implements OnInit {
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  sortBy: string = 'date';
  direction: string = 'desc';
  search: string = '';

  private searchSubject = new Subject<string>();

  sortOptions =[
    { value: 'description', label: 'Description' },
    { value: 'amount', label: 'Amount' },
    { value: 'category', label: 'Category' },
    { value: 'date', label: 'Date' }
  ]

  @Input() expenses: Expense[] = [];
  @Output() expenseDeleted = new EventEmitter<string>();

  constructor(private expenseService: ExpenseService){}

  ngOnInit(): void{
    this.getAllSearchedExpenses();

    // Debounce search input
    this.searchSubject.pipe(
      debounceTime(500),          // wait 500ms after user stops typing
      distinctUntilChanged(),    // only search if value changed
   ).subscribe(searchTerm => {
    this.page = 0;
    this.getAllSearchedExpenses();
   });
  }

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

  getAllSearchedExpenses() {
    this.expenseService.getSearchedExpense(this.page, this.size, this.sortBy, this.direction, this.search)
    .subscribe({
      next: (response) => {
        this.expenses = response.content;
        this.totalPages = response.totalPages;
      },
      error: (error) => {
        console.error('Error fetching expenses:', error);
        this.expenses = [];
        this.totalPages = 0;

        // show user-friendly message or handle error appropriately
        if(error.status === 403){
          console.error('Access forbidden: Please log in again.');
        }
      }
    })
  }

  onSortChange(){
    this.page = 0; // Reset to first page on sort change
    this.getAllSearchedExpenses();
  }

  // Updated method name of search 
  onSearchInput(){
    this.searchSubject.next(this.search);
  }

  clearSearch(){
    this.search = '';
    this.page = 0;
    this.getAllSearchedExpenses();
  }

  toggleSortDirection(column: string){
    if(this.sortBy === column) {
      this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    }
    else{
      this.sortBy = column;
      this.direction = 'asc';
    }
    this.page = 0;
    this.getAllSearchedExpenses();
  }

  goToPage(pageNumber: number){
    if(pageNumber >=0 && pageNumber < this.totalPages){
      this.page = pageNumber;
      this.getAllSearchedExpenses();
    }
  }

  getSortIcon(column: string): string {
    if(this.sortBy === column){
      return 'bi-arrow-down-up';
    }
    return this.direction === 'asc' ? 'bi-caret-up-full' : 'bi-caret-down-fill';
  }
}

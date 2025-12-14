import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ExpenseService } from '../expense.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-expense-update-modal',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './expense-update-modal.component.html',
  styleUrl: './expense-update-modal.component.css'
})
export class ExpenseUpdateModalComponent implements OnInit, OnDestroy {
  @Input() expense: any | null = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() expenseUpdated = new EventEmitter<void>();
  message = "";
  gForm!: FormGroup;
  loading = false;
  categories = ['Food', 'Travel', 'Shopping', 'Utilities', 'Other'];

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService
  ) { }

  ngOnInit(): void {
    this.gForm = this.fb.group({
      description: ['', [Validators.required, Validators.minLength(1)]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      category: ['', Validators.required],
      date: ['', Validators.required]
    });
    this.prefill();
  }

  prefill() {
    if (!this.expense) return;

    // const dateVal = this.expense.date ? (this.expense.date.length > 10 ? this.expense.date.substring(0, 10): this.expense.date) : '';
    const dateVal = this.expense.date ? this.expense.date.substring(0, 10) : '';
    this.gForm.patchValue({
      description: this.expense.description ?? '',
      amount: this.expense.amount ?? 0,
      category: this.expense.category ?? '',
      date: dateVal
    });
  }

  close() {
    this.closeModal.emit();
  }

  ngOnDestroy(): void {
    document.body.style.overflow = '';
  }

  saveChanges() {
    if (this.gForm.invalid || !this.expense) {
      this.gForm.markAllAsTouched();
      return;
    }
    this.loading = true;

    const updatedExpense = this.gForm.value;
    this.expenseService.updateExpense(String(this.expense.id), updatedExpense)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.expenseUpdated.emit();
          this.closeModal.emit();
        },
        error: (err) => {
          console.error("Expense update error : ", err);
          alert("Failed to update expense. Try again later.");
        }
      });
  }

  get description() { return this.gForm.get('description'); }
  get amount() { return this.gForm.get('amount'); }
  get category() { return this.gForm.get('category'); }
  get date() { return this.gForm.get('date'); }

}

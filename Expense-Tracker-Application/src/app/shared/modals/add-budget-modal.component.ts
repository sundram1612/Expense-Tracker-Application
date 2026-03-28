import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ExpenseService } from '../../expense.service';

@Component({
  selector: 'app-add-budget-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="modal fade show" tabindex="-1" style="display: block; background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0 shadow-lg">
          
          <div class="modal-header border-bottom-0 pb-0 pt-4 px-4 position-relative">
             <button type="button" class="btn-close position-absolute" style="right: 20px; top: 20px;" (click)="onClose()"></button>
             <h5 class="modal-title fw-bold text-center w-100 text-body">Set Monthly Budget</h5>
          </div>

          <div class="modal-body px-4 pb-4 pt-3">
             <p class="text-center text-body-secondary small mb-4">Define your total monthly spending limit across all categories.</p>
             
             <form [formGroup]="budgetForm" (ngSubmit)="onSubmit()">
                <!-- Amount -->
                <div class="mb-4">
                  <label class="form-label fw-semibold text-body-secondary small mb-1">Total Amount (&#8377;)</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0">&#8377;</span>
                    <input type="number" class="form-control border-start-0 ps-0 bg-light" formControlName="amount" placeholder="0.00" style="box-shadow: none;">
                  </div>
                </div>

                <div class="d-grid mt-4">
                   <button type="submit" class="btn btn-primary py-2 fw-medium shadow-sm" [disabled]="!budgetForm.valid || isSubmitting">
                      <span *ngIf="isSubmitting" class="spinner-border spinner-border-sm me-2"></span>
                      Save Budget
                   </button>
                </div>
             </form>
          </div>

        </div>
      </div>
    </div>
  `
})
export class AddBudgetModalComponent {
  @Output() closeModal = new EventEmitter<void>();
  @Output() budgetUpdated = new EventEmitter<void>();

  budgetForm: FormGroup;
  isSubmitting = false;

  constructor(private fb: FormBuilder, private expenseService: ExpenseService) {
    this.budgetForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0)]]
    });
  }

  onClose() {
    this.closeModal.emit();
  }

  onSubmit() {
    if (this.budgetForm.valid) {
      this.isSubmitting = true;
      const amount = this.budgetForm.value.amount;

      this.expenseService.updateBudget(amount).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.budgetUpdated.emit();
          this.onClose();
        },
        error: (err: any) => {
          this.isSubmitting = false;
          console.error('Error updating budget:', err);
          alert('Failed to update budget. Try again.');
        }
      });
    }
  }
}

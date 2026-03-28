import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../expense.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-add-income-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="modal-backdrop fade show overlay"></div>
    <div class="modal fade show d-block" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered modal-md">
        <div class="modal-content border-0 rounded-4 shadow-lg">
          
          <div class="modal-header border-bottom-0 pb-0">
             <h5 class="modal-title fw-bold text-center w-100 text-body">Add Income</h5>
             <button class="btn-close position-absolute end-0 me-3" (click)="close()"></button>
          </div>
          
          <div class="modal-body px-4 py-4">
            <form [formGroup]="form" (ngSubmit)="save()">
              
              <div class="mb-3 d-flex align-items-center justify-content-between">
                 <label class="form-label mb-0 fw-medium text-secondary">Amount</label>
                 <div class="input-group" style="width: 200px;">
                    <span class="input-group-text bg-body border-end-0 text-secondary">&#8377;</span>
                    <input type="number" class="form-control border-start-0 ps-0" formControlName="amount" placeholder="0.00">
                 </div>
              </div>

              <div class="mb-3 d-flex align-items-center justify-content-between">
                 <label class="form-label mb-0 fw-medium text-secondary">Source</label>
                 <select class="form-select fw-medium text-body" formControlName="source" style="width: 200px;">
                    <option value="Salary">Salary</option>
                    <option value="Business">Business</option>
                    <option value="Investments">Investments</option>
                    <option value="Other">Other</option>
                 </select>
              </div>

              <div class="mb-4 d-flex align-items-center justify-content-between">
                 <label class="form-label mb-0 fw-medium text-secondary">Date</label>
                 <input type="date" class="form-control" formControlName="date" style="width: 200px;">
              </div>

              <hr class="border-secondary opacity-10">

              <div class="mb-4">
                 <label class="form-label fw-medium text-secondary">Notes</label>
                 <input type="text" class="form-control" formControlName="notes" placeholder="Add a note" style="font-style: italic;">
              </div>
              
              <hr class="border-secondary opacity-10 mb-4 px-0 mx-n4" style="margin-left: -1.5rem; margin-right: -1.5rem;">

              <div class="d-flex justify-content-between gap-3">
                 <button type="button" class="btn btn-light flex-grow-1 fw-medium text-secondary" style="background-color: #dbe0e6;" (click)="close()">Cancel</button>
                 <button type="submit" class="btn flex-grow-1 fw-bold text-white" style="background-color: #3fbc4a;" [disabled]="form.invalid || loading">
                    <span *ngIf="!loading">Add Income</span>
                    <span *ngIf="loading" class="spinner-border spinner-border-sm"></span>
                 </button>
              </div>

            </form>
          </div>
          
        </div>
      </div>
    </div>
  `,
  styles: [`
    .overlay { background-color: rgba(0,0,0,0.2) !important; }
    .form-control, .form-select, .input-group-text { border-color: #e2e8f0; }
    .form-control:focus, .form-select:focus { box-shadow: none; border-color: #cbd5e1; }
  `]
})
export class AddIncomeModalComponent {
  @Output() closeModal = new EventEmitter<void>();
  @Output() incomeAdded = new EventEmitter<void>();
  
  form: FormGroup;
  loading = false;

  constructor(private fb: FormBuilder, private expenseService: ExpenseService) {
    this.form = this.fb.group({
      amount: [0, [Validators.required, Validators.min(0.01)]],
      source: ['Salary', Validators.required],
      date: [new Date().toISOString().substring(0, 10), Validators.required],
      notes: ['']
    });
  }

  close() {
    this.closeModal.emit();
  }

  save() {
    if (this.form.invalid) return;
    this.loading = true;
    const val = this.form.value;
    const payload = {
      amount: val.amount,
      category: val.source, 
      date: val.date,
      description: val.notes, 
      type: 'INCOME'
    };

    this.expenseService.addExpense(payload).pipe(finalize(() => this.loading = false)).subscribe({
      next: () => {
        this.incomeAdded.emit();
        this.close();
      },
      error: (err) => {
        alert('Failed to add income');
        console.error(err);
      }
    });
  }
}

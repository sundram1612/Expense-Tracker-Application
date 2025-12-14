import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseUpdateModalComponent } from './expense-update-modal.component';

describe('ExpenseUpdateModalComponent', () => {
  let component: ExpenseUpdateModalComponent;
  let fixture: ComponentFixture<ExpenseUpdateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpenseUpdateModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseUpdateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

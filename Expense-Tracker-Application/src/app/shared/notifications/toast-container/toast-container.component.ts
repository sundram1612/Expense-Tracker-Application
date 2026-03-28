import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../toast.service';
import { Toast } from '../toast.model';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.component.html',
  styleUrl: './toast-container.component.css',
  animations: [
    trigger('toastAnimation', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('300ms cubic-bezier(0.18, 0.89, 0.32, 1.28)', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ])
  ]
})
export class ToastContainerComponent implements OnInit {
  toasts$;

  constructor(private toastService: ToastService) {
    this.toasts$ = this.toastService.toasts$;
  }

  ngOnInit(): void { }

  remove(id: number) {
    this.toastService.remove(id);
  }

  getIcon(type: string): string {
    switch (type) {
      case 'success': return 'bi-check-circle-fill text-success';
      case 'error': return 'bi-x-circle-fill text-danger';
      case 'warning': return 'bi-exclamation-triangle-fill text-warning';
      default: return 'bi-info-circle-fill text-info';
    }
  }
}

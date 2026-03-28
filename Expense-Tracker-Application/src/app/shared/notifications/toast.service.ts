import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Toast, ToastType } from './toast.model';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toasts: Toast[] = [];
  private toastsSubject = new BehaviorSubject<Toast[]>([]);
  public toasts$ = this.toastsSubject.asObservable();
  private nextId = 0;

  show(message: string, type: ToastType = 'info', title?: string, duration: number = 5000) {
    const id = this.nextId++;
    const toast: Toast = { id, message, type, title, duration };
    this.toasts.push(toast);
    this.toastsSubject.next([...this.toasts]);

    if (duration > 0) {
      setTimeout(() => this.remove(id), duration);
    }
  }

  success(message: string, title?: string, duration: number = 5000) {
    this.show(message, 'success', title || 'Success', duration);
  }

  error(message: string, title?: string, duration: number = 5000) {
    this.show(message, 'error', title || 'Error', duration);
  }

  info(message: string, title?: string, duration: number = 5000) {
    this.show(message, 'info', title || 'Info', duration);
  }

  warning(message: string, title?: string, duration: number = 5000) {
    this.show(message, 'warning', title || 'Warning', duration);
  }

  remove(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.toastsSubject.next([...this.toasts]);
  }
}

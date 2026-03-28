import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./home/home.component')
      .then(m => m.HomeComponent)
  },
  {
    path: 'login', loadComponent: () => import('./auth/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register', loadComponent: () => import('./auth/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./auth/forgot-password/forgot-password.component')
        .then(m => m.ForgotPasswordComponent)
  },
  {
    path: 'reset-password/:token',
    loadComponent: () =>
      import('./auth/reset-password/reset-password.component')
        .then(m => m.ResetPasswordComponent)
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    loadComponent: () => import('./transactions/transactions.component')
        .then(m => m.TransactionsComponent)
  },
  {
    path: 'budgets',
    canActivate: [authGuard],
    loadComponent: () => import('./budgets/budgets.component')
        .then(m => m.BudgetsComponent)
  },
  {
    path: 'reports',
    canActivate: [authGuard],
    loadComponent: () => import('./reports/reports.component')
        .then(m => m.ReportsComponent)
  }

];

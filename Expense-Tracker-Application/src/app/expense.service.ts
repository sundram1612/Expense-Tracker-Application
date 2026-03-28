import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface Expense {
  id?: string;
  description: string;
  amount: number;
  category: string;
  type?: string;
  paymentMethod?: string;
  date: string;
}

@Injectable({
  providedIn: 'root'
})

export class ExpenseService {
  private apiUrl = 'http://localhost:8080/api/expenses';

  constructor(private http: HttpClient) { }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    return new HttpHeaders({
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  getExpenses(): Observable<Expense[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<any>(`${this.apiUrl}`, { headers })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<Expense[]>('getExpenses'))
      );
  }

  addExpense(expense: Expense): Observable<Expense> {
    const headers = this.getAuthHeaders();

    return this.http.post<any>(`${this.apiUrl}`, expense, { headers })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<Expense>('addExpense'))
      );
  }

  updateExpense(id: string, expenseData: any){
    const headers = this.getAuthHeaders();

    return this.http.put<any>(`${this.apiUrl}/update-expense/${id}`, expenseData, { headers })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<Expense>('updateExpense'))
      );
  }

  deleteExpense(id: string): Observable<void> {
    const headers = this.getAuthHeaders();

    return this.http.delete<any>(`${this.apiUrl}/${id}`, { headers })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<void>('deleteExpense'))
      );
  }
  
  getSearchedExpense(page: number, size: number, sortBy: string, direction: string, search: string='') {
    const headers = this.getAuthHeaders();
    const params = {
      page: page.toString(),
      size: size.toString(),
      sortBy,
      direction,
      search: search
    };
  
    return this.http.get<any>(`${this.apiUrl}/search-sort`, { headers, params })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<any>('getAllSearchedExpenses'))
      );
  }

  getDashboardData(year?: number, month?: number): Observable<any> {
    const headers = this.getAuthHeaders();
    let url = `${this.apiUrl}/dashboard`;
    if (year && month) {
       url += `?year=${year}&month=${month}`;
    }
    return this.http.get<any>(url, { headers })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<any>('getDashboardData'))
      );
  }

  updateBudget(amount: number): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.put<any>(`${this.apiUrl}/budget?amount=${amount}`, {}, { headers })
      .pipe(
        map(res => res.data),
        catchError(this.handleError<any>('updateBudget'))
      );
  }

  downloadPdf(from: string, to: string, reportType: string): Observable<Blob> {
    const headers = this.getAuthHeaders();
    const params = { from, to, reportType };
    return this.http.get(`${this.apiUrl}/report/pdf`, { 
      headers, 
      params, 
      responseType: 'blob' 
    });
  }

  downloadExcel(from: string, to: string, reportType: string): Observable<Blob> {
    const headers = this.getAuthHeaders();
    const params = { from, to, reportType };
    return this.http.get(`${this.apiUrl}/report/excel`, { 
      headers, 
      params, 
      responseType: 'blob' 
    });
  }

  requestAsyncReport(payload: any): Observable<any> {
    const headers = this.getAuthHeaders();
    // Assuming Kafka controller is at http://localhost:8080/api/kafka
    return this.http.post<any>(`http://localhost:8080/api/kafka/report/async`, payload, { 
      headers, 
      responseType: 'text' as 'json' 
    }).pipe(
      catchError(this.handleError<any>('requestAsyncReport'))
    );
  }

  private handleError<T>(operation = 'operation') {
    return (error: HttpErrorResponse): Observable<T> => {
      console.error(`${operation} failed:`, error);

      let errorMessage = 'Something went wrong while contacting the backend server.';
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.statusText) {
        errorMessage = `${error.status}: ${error.statusText}`;
      }

      return throwError(() => new Error(errorMessage));
    };
  }

}


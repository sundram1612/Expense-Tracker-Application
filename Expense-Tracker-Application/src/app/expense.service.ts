import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Expense {
  id?: string;
  description: string;
  amount: number;
  category: string;
  date: string;
}

@Injectable({
  providedIn: 'root'
})

export class ExpenseService {
  private apiUrl = 'http://localhost:8080/api/expenses'; 

  constructor(private http: HttpClient) {}

private getAuthHeaders(): HttpHeaders {
  // const token = localStorage.getItem('token');
  // return new HttpHeaders({
  //   'Authorization': `Bearer ${token}`
  // });

  // if(typeof window === 'undefined' || typeof localStorage === 'undefined'){
  //   return new HttpHeaders();
  // }

  const token = localStorage.getItem('token');

  return new HttpHeaders({
    'Authorization': token ? `Bearer ${token}` : ''
  });
}

  getExpenses(): Observable<Expense[]> {
    const headers = this.getAuthHeaders();

    return this.http.get<Expense[]>(`${this.apiUrl}`, { headers })
    .pipe(catchError(this.handleError<Expense[]>('getExpenses')));
  }

  addExpense(expense: Expense): Observable<Expense> {
    const headers = this.getAuthHeaders();

    // return this.http.post<Expense>(this.apiUrl, expense, { headers })
    return this.http.post<Expense>(`${this.apiUrl}`, expense, { headers })
    .pipe(catchError(this.handleError<Expense>('addExpense')));
  }

  deleteExpense(id: string): Observable<void> {
    const headers = this.getAuthHeaders();

    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers })
    .pipe(catchError(this.handleError<void>('deleteExpense')));
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

      // const errMsg = error.error?.message || error.statusText || 'Server error';
      // return throwError(() => new Error(errMsg));
    };
}

}

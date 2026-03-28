import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, map, tap } from "rxjs";
import { RegisterRequest } from "./models/register-request.model";
import { AuthResponse } from "./models/auth-response.model";
import { LoginRequest } from "./models/login-request.model";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private baseUrl = 'http://localhost:8080/auth';
    private currentUserSubject = new BehaviorSubject<string | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();
    sendResetLink: any;

    constructor(private http: HttpClient) {
        const name = this.isBrowser() ? localStorage.getItem('name') : null;
        if (name) {
            this.currentUserSubject.next(name);
        }
    }
    private isBrowser(): boolean {
        return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
    }

    register(data: RegisterRequest): Observable<string> {
        return this.http.post<any>(this.baseUrl + '/register', data).pipe(
            map((res: any) => res.message || 'Registered successfully')
        );
    }

    login(data: LoginRequest): Observable<AuthResponse> {
        return this.http.post<any>(this.baseUrl + '/login', data).pipe(
            map((res: any) => res.data as AuthResponse),
            tap((response: AuthResponse) => {
                if (this.isBrowser()) {
                    localStorage.setItem('token', response.token);

                    if (response.refreshToken) {
                        localStorage.setItem('refreshToken', response.refreshToken);
                    }

                    localStorage.setItem('name', response.user.name);
                    this.currentUserSubject.next(response.user.name);
                }
            }));
    }

    logout() {
        if (this.isBrowser()) {
            localStorage.removeItem('token');
            localStorage.removeItem('name');
        }
        this.currentUserSubject.next(null);
    }

    getToken(): string | null {
        return this.isBrowser() ? localStorage.getItem('token') : null;
    }

    sendResetLinkToUser(email: string) {
        return this.http.post<any>('http://localhost:8080/auth/forgot-password', { email }).pipe(
            map((res: any) => res.message || 'Reset link sent')
        );
    }

    resetPassword(token: string, newPassword: string) {
        return this.http.post<any>(`http://localhost:8080/auth/reset-password/${token}`, { newPassword }).pipe(
            map((res: any) => res.message || 'Password reset successful')
        );
    }

}
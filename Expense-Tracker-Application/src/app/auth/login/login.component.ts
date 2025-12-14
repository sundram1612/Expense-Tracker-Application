import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../auth.service';
import { ForgotPasswordComponent } from '../forgot-password/forgot-password.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loading=false;
  errorMsg: string | null = null;
  loginForm!: FormGroup;
  
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}
  ngOnInit(): void {
    this.loginForm = this.fb.group
    ({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    remember: [true]
  });} 


  onSubmit(){
    if(this.loginForm.invalid) return;

    this.errorMsg = null;
    this.loading = true;

    const payload = {
      email: this.loginForm.get('email')?.value ?? '',
      password: this.loginForm.get('password')?.value ?? ''

    };

    this.auth.login(payload)
    .pipe(finalize(() => (this.loading = false)))
    .subscribe({
      next: () => {
        const returnUrl = this.router.routerState.snapshot.root.queryParams['returnUrl'] || '/';
        this.router.navigate([returnUrl]);  // this will redirect the user to home page after login
      },
      error: (err) => {
        console.error('Login error', err);
        
        if(err?.status === 401){
          this.errorMsg = 'Invalid Email or Password. Please try again.';
        }
        else if(err?.status === 0){
          this.errorMsg = 'Server is not reachable. Try agian later.';
        }
        else{
          this.errorMsg = err?.error?.message || 'Login failed. Please try again.';
        }
      }
    });
  }
}

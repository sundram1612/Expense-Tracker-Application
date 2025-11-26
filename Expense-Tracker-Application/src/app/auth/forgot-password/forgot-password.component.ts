import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
  forgotForm: any;
  loading: boolean = false;
  
  constructor(
    private fb: FormBuilder, 
    private authService: AuthService, 
    private router: Router) {
    this.forgotForm = this.fb.group({
      email:['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.forgotForm.invalid) {
      Swal.fire('Please enter valid email');
      return;
    }

    this.loading = true;
    const email = this.forgotForm.value.email;

    this.authService.sendResetLinkToUser(email).subscribe({
      next: () => {
        Swal.fire('Success', 'Reset link sent to your email', 'success');
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        Swal.fire('Error', err.error?.message || 'Something went wrong', 'error');
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

}

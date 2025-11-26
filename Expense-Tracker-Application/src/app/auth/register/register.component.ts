import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  successMsg = '';
  errorMsg = '';

  registerForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ){
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  OnSubmit(){
    if(!this.registerForm.valid) return;

    this.successMsg = '';
    this.errorMsg = '';

    const payload = {
      name: this.registerForm.get('name')!.value!,
      email: this.registerForm.get('email')!.value!,
      password: this.registerForm.get('password')!.value!
    };

    this.authService.register(payload).subscribe({
      next: (res) => {
        this.successMsg = res;
        this.registerForm.reset();
      },
      error: (err) => {
        this.errorMsg = err.error || 'Registration Failed . Please try again.';
      }
    });
  }

}

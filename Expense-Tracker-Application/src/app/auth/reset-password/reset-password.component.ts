
import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, Form, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent {
  message = "";
  token: string | null = null;
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService) {      
      this.form = this.fb.group({
        newPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required]
      });
    }

    ngOnInit(){
      this.token = this.route.snapshot.paramMap.get('token');
    }  

    submit(){
      if(!this.token){
        this.message = "Invalid or expired link";
        return;
      }

      const { newPassword, confirmPassword } = this.form.value;
      if(newPassword !== confirmPassword){
        this.message = "Passwords do not match!";
        return;
      }

      this.auth.resetPassword(this.token, newPassword!).subscribe({
        next: () => {
          this.message = "Password changed Successfully! Redirecting...";
          setTimeout(() => this.router.navigate(["/login"]), 1500);
        },
        error: (err) => {
          console.error("Reset Password error: ", err);
          this.message = "Invalid or expired link.";
        }
      })
    }

}

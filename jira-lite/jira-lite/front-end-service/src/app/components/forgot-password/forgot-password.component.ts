import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {RouterLink} from "@angular/router";

@Component({
  standalone: true,
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  imports: [FormsModule, CommonModule, RouterLink]
})
export class ForgotPasswordComponent {
  email = '';
  success = false;
  error = '';

  constructor(private authService: AuthService) {}

  submit() {
    this.authService.forgotPassword(this.email).subscribe({
      next: () => this.success = true,
      error: err => this.error = err.error || 'Сталась помилка. Спробуйте ще раз.'
    });
  }
}

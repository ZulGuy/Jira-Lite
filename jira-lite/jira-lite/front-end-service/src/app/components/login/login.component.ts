// login.component.ts
import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [FormsModule, CommonModule, RouterLink],
})
export class LoginComponent {
  username = '';
  password = '';
  error: string | null = null;
  loginFailed = false;

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    localStorage.setItem('tenantId', 'public');

    this.auth.login({email: this.username, password: this.password}).subscribe({
      next: () => {
        this.auth.redirectUrl = null;
        localStorage.removeItem('recentProject');
        window.location.href = '/';
      },
      error: () => {
        this.loginFailed = true;
        alert('Не вдалося авторизуватись. Перевірте логін та пароль.');
      }
    });
  }
}

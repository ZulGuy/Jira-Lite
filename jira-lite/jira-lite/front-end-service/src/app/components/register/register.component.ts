import {Component} from "@angular/core";
import {AuthService} from "../../services/auth.service";
import {Router, RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import Swal from "sweetalert2";

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [FormsModule, CommonModule, RouterLink]
})
export class RegisterComponent {
  form = {
    email: '',
    password: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  submit() {
    this.authService.register(this.form).subscribe({
      next: () => {
        Swal.fire({text: 'Registered!', icon: 'success'});
        setTimeout(() => this.router.navigate(['/login']), 100); // ← тестова затримка
      },
      error: err => {
        console.error('Register error:', err);
        const msg = typeof err.error === 'string'
          ? err.error
          : err.error?.message || 'Registration failed';
        Swal.fire({text: msg, icon: 'error'});
      }
    });
  }
}

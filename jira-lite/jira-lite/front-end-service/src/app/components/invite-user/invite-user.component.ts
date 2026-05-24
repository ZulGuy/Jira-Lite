import {Component} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from "../../../environments/environment";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";

@Component({
  standalone: true,
  selector: 'app-invite-user',
  templateUrl: './invite-user.component.html',
  styleUrls: ['./invite-user.component.scss'],
  imports: [FormsModule, CommonModule]
})
export class InviteUserComponent {
  email = '';
  message = '';
  private api = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {
  }

  sendInvitation() {
    if (!this.email.trim()) return;

    this.http.post(`${this.api}/invitations/send?email=${this.email}`, null, {
      withCredentials: true,
      responseType: 'text'
    })
    .subscribe({
      next: req => {
        this.message = req;
        this.email = '';
      },
      error: err => {
        this.message = 'Помилка: ' + (err.error || 'невідомо');
      }
    });
  }
}

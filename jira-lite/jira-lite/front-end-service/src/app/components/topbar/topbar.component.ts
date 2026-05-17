import {Component} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {CommonModule} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";

@Component({
  standalone: true,
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss'],
  imports: [CommonModule, RouterLink, RouterLinkActive]
})
export class TopbarComponent {

  constructor(private authService: AuthService) {}

  isPublicPage(): boolean {
    return (localStorage.getItem('tenantId') ?? 'public') === 'public';
  }

  canManageUsers(): boolean {
    return this.authService.isSystemAdmin();
  }
}

import {Component, signal} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {CommonModule} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {TenantDTO} from "../../types/api.types";
import {TennantService} from "../../services/tennant.service";

@Component({
  standalone: true,
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss'],
  imports: [CommonModule, RouterLink, RouterLinkActive]
})
export class TopbarComponent {

  tenant = signal<TenantDTO | null>(null);
  tenantLoading = signal(true);
  switching = signal(false);

  constructor(private authService: AuthService, private tennantService: TennantService) {}

  isPublicPage(): boolean {
    return (localStorage.getItem('tenantId') ?? 'public') === 'public';
  }

  canManageUsers(): boolean {
    return this.authService.isSystemAdmin();
  }

  switchTenant(): void {
    this.switching.set(true);
    this.authService.switchTenant().subscribe({
      next: () => { window.location.href = '/'; },
      error: () => this.switching.set(false)
    });
  }

  ngOnInit(): void {
    this.tennantService.getByName("public").subscribe({
      next: (t) => {
        this.tenant.set(t);
    }
    });
  }
}

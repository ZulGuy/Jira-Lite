import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { TennantService } from '../../services/tennant.service';
import { TenantDTO, UserDTO } from '../../types/api.types';
import { CreateTenantModalComponent } from '../create-tenant-modal/create-tenant-modal.component';

@Component({
  standalone: true,
  selector: 'app-public-home-page',
  templateUrl: './public-home-page.component.html',
  styleUrls: ['./public-home-page.component.scss'],
  imports: [CommonModule, CreateTenantModalComponent],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PublicHomePageComponent implements OnInit {
  user: UserDTO | null = null;
  tenant = signal<TenantDTO | null>(null);
  tenantLoading = signal(true);
  switching = signal(false);
  disabling = signal(false);
  showCreateModal = false;

  constructor(
    private authService: AuthService,
    private tennantService: TennantService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
    const userTennant = this.user?.tennant;

    if (userTennant && userTennant !== 'public') {
      this.tennantService.getByName(userTennant).subscribe({
        next: (t) => {
          this.tenant.set(t);
          this.tenantLoading.set(false);
        },
        error: () => this.tenantLoading.set(false)
      });
    } else {
      this.tenantLoading.set(false);
    }
  }

  /** User already belongs to a tenant (as creator or invitee). */
  get hasTenant(): boolean {
    return !!this.user?.tennant && this.user.tennant !== 'public';
  }

  /** User is the admin (creator) of the tenant. */
  get isAdmin(): boolean {
    return this.tenant()?.adminId === this.user?.id;
  }

  switchTenant(): void {
    this.switching.set(true);
    this.authService.switchTenant().subscribe({
      next: () => { window.location.href = '/'; },
      error: () => this.switching.set(false)
    });
  }

  disableTenant(): void {
    const t = this.tenant();
    if (!t) return;
    this.disabling.set(true);
    this.tennantService.delete(t.id).subscribe({
      next: () => { window.location.href = '/'; },
      error: () => this.disabling.set(false)
    });
  }

  onTenantCreated(): void {
    window.location.href = '/';
  }
}

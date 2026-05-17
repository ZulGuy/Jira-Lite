import { ChangeDetectionStrategy, Component, EventEmitter, Output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TennantService } from '../../services/tennant.service';
import { TenantDTO } from '../../types/api.types';

const VALID_NAME = /^[a-z][a-z0-9_]{2,29}$/;

@Component({
  standalone: true,
  selector: 'app-create-tenant-modal',
  templateUrl: './create-tenant-modal.component.html',
  styleUrls: ['./create-tenant-modal.component.scss'],
  imports: [FormsModule, CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateTenantModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() tenantCreated = new EventEmitter<TenantDTO>();

  name = '';
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(private tennantService: TennantService) {}

  get nameError(): string | null {
    if (!this.name) return null;
    if (!/^[a-z]/.test(this.name)) return 'Must start with a lowercase letter.';
    if (!VALID_NAME.test(this.name)) return 'Only lowercase letters, digits, and underscores. Min 3 chars.';
    if (this.name === 'public' || this.name.startsWith('pg_')) return 'This name is reserved.';
    return null;
  }

  get canSubmit(): boolean {
    return !!this.name.trim() && !this.nameError && !this.loading();
  }

  submit(): void {
    if (!this.canSubmit) return;
    this.loading.set(true);
    this.error.set(null);

    this.tennantService.create(this.name.trim()).subscribe({
      next: (tenant) => {
        this.loading.set(false);
        this.tenantCreated.emit(tenant);
        this.close.emit();
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message ?? 'Failed to create tenant. Please try again.');
      }
    });
  }

  closeModal(): void {
    if (!this.loading()) this.close.emit();
  }
}

import { Component, EventEmitter, Input, Output, input, signal, effect } from '@angular/core';
import {ProjectUserService} from "../../services/project-user.service";
import {ProjectRole} from "../../types/api.types";
import {ProjectUserDTO} from "../../types/api.types";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  standalone: true,
  selector: 'app-role-modal',
  templateUrl: './role-modal.component.html',
  styleUrls: ['./role-modal.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class RoleModalComponent {
  user = input<ProjectUserDTO>();
  @Input() projectId: number;
  @Output() close = new EventEmitter<void>();
  @Output() rolesChanged = new EventEmitter<void>();

  availableRoles: ProjectRole[] = ['ADMIN', 'EDITOR', 'VIEWER'];
  selectedRole: ProjectRole;
  displayedRoles = signal<ProjectRole[]>([]);

  constructor(private projectUserService: ProjectUserService) {
    effect(() => {
      this.displayedRoles.set(this.user().roles ? this.user().roles : []);
    });
  }

  removeRole(role: ProjectRole) {
    const remaining = this.displayedRoles().filter(r => r !== role);
    this.projectUserService.updateRoles(this.projectId, this.user().id, remaining)
      .subscribe(() => {
        this.displayedRoles.set(remaining);
        this.rolesChanged.emit();
      });
  }

  addRole() {
    if (this.selectedRole) {
      const updated = [...this.displayedRoles(), this.selectedRole];
      this.projectUserService.updateRoles(this.projectId, this.user().id, updated)
        .subscribe(() => {
          this.displayedRoles.set(updated);
          this.rolesChanged.emit();
        });
      this.selectedRole = null;
    }
  }

  closeModal() {
    this.close.emit();
  }

}

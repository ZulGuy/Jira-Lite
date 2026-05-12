// src/app/types/api.types.ts

export type ProjectRole = 'ADMIN' | 'EDITOR' | 'VIEWER';
export type Role = 'ROLE_ADMIN' | 'ROLE_USER';

export interface ProjectDTO {
  id: number;
  name: string;
  description: string;
}

export interface ProjectUserDTO {
  id: number;   // user id!
  name: string;
  email: string;
  active: boolean;
  roles: ProjectRole[];
}

export interface UserDTO {
  id: number;
  name: string;
  email: string;
  active: boolean;
  role: Role;
  tenant: TenantDTO;
}

export interface CommentDTO {
  id: number;
  description: string;
  authorName: string;
}

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface TaskDTO {
  id: number;
  summary: string;
  description: string;
  assignee?: UserDTO;
  initiator?: UserDTO;
  status: TaskStatus;
  projectId: number;
}

export interface TaskTypeDTO {
  id: number;
  name: string;
}

export type FieldType = 'SELECT' | 'TEXT' | 'DATE' | 'NUMBER';

export interface CustomFieldDTO {
  id: number;
  name: string;
  value?: string;
  type: FieldType;
}

export interface TenantDTO {
  id: string;
  name: string;
  status: string;
  admin: UserDTO;
}


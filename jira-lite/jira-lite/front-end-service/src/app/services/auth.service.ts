import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import {Observable, of} from 'rxjs';
import {catchError, map, tap} from "rxjs/operators";
import {ProjectUserDTO, UserDTO} from "../types/api.types";
import {ProjectUserService} from "./project-user.service";

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = `${environment.apiUrl}/api/auth`;
  public redirectUrl: string | null = null;
  private user: UserDTO & { role: string } | null = null;
  projectUsers: ProjectUserDTO[] = null;

  constructor(private http: HttpClient, private projectUserService: ProjectUserService) {}

  setCurrentUser(user: UserDTO & { role: string }) {
    this.user = user;
  }

  getCurrentUser() {
    return this.user;
  }

  login(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.api}/login`, credentials, { withCredentials: true }).pipe(
      tap(response => {
        const payload = JSON.parse(atob(response.token.split('.')[1]));
        localStorage.setItem('tenantId', payload.tennantId || 'public');
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.api}/logout`, {}, { withCredentials: true });
  }

  register(data: { email: string, password: string }): Observable<any> {
    return this.http.post(`${this.api}/register`, data, {
      withCredentials: true,
      responseType: 'text'
    });
  }

  switchTenant(): Observable<string> {
    return this.http.get(`${this.api}/switch-tenant`, {
      withCredentials: true,
      responseType: 'text'
    }).pipe(
      tap(msg => {
        const match = msg.match(/Tenant switched to (.+)$/);
        if (match) localStorage.setItem('tenantId', match[1].trim());
      })
    );
  }

  isAuthenticated(): Observable<any> {
    return this.http.get<UserDTO>(`${environment.apiUrl}/api/users/current`, { withCredentials: true }).pipe(
      tap(user => this.setCurrentUser(user)), // можна роль доставати з claims, якщо хочеш
      map(() => true),
      catchError(() => of(false))
    );
  }

  isSystemAdmin(): boolean {
    return this.user?.role === 'ROLE_ADMIN';
  }

  canEditTask(projectId: number): Observable<boolean> {
    if (this.user?.role === 'ROLE_ADMIN') {
      return of(true);
    }

    return this.projectUserService.getProjectUsers(projectId).pipe(
      map(users => {
        const currentUser = this.user;
        const current = users.find(u => u.id === currentUser?.id);
        return !!current && (current.roles.includes('ADMIN') || current.roles.includes('EDITOR'));
      }),
      catchError(() => of(false))
    );
  }

  forgotPassword(email: string): Observable<void> {
    return this.http.post<void>(`${this.api}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.api}/reset-password`, { token, newPassword });
  }


}

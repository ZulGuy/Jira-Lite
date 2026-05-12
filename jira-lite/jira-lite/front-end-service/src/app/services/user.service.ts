import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { UserDTO } from '../types/api.types';

@Injectable({ providedIn: 'root' })
export class UserService {
  private api = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) {}

  getAll(query = ''): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.api}?query=${query}`, { withCredentials: true });
  }

  // get current user
  getCurrentUser(): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.api}/current`, { withCredentials: true });
  }

  // get user by id
  getUserById(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.api}/${id}`, { withCredentials: true });
  }

  // toggle active status
  toggleActive(id: number): Observable<UserDTO> {
    return this.http.patch<UserDTO>(`${this.api}/${id}/toggle-active`, {}, { withCredentials: true });
  }

  createUser(user: UserDTO): Observable<UserDTO> {
    return this.http.post<UserDTO>(this.api, user, { withCredentials: true });
  }

  deleteUser(id: number) {
    return this.http.delete(`${this.api}/${id}`, { withCredentials: true });
  }

  updateUser(user: UserDTO): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.api}/${user.id}`, user, { withCredentials: true });
  }
}

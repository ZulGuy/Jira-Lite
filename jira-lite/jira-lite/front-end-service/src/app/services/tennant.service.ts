import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { TenantDTO } from '../types/api.types';

@Injectable({ providedIn: 'root' })
export class TennantService {
  private api = `${environment.apiUrl}/api/tennants`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TenantDTO[]> {
    return this.http.get<TenantDTO[]>(this.api, { withCredentials: true });
  }

  getByName(name: string): Observable<TenantDTO> {
    return this.http.get<TenantDTO>(`${this.api}/${name}`, { withCredentials: true });
  }

  create(name: string): Observable<TenantDTO> {
    const params = new HttpParams().set('name', name);
    return this.http.post<TenantDTO>(this.api, null, { params, withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`, { withCredentials: true });
  }
}

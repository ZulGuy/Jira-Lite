import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { CommentDTO } from '../types/api.types';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private api = `${environment.apiUrl}/api/comments`;

  constructor(private http: HttpClient) {}

  getByTaskId(taskId: number): Observable<CommentDTO[]> {
    return this.http.get<CommentDTO[]>(`${this.api}/task/${taskId}`, { withCredentials: true });
  }

  addComment(taskId: number, description: string): Observable<CommentDTO> {
    return this.http.post<CommentDTO>(
      this.api,
      { task: { id: taskId }, description },
      { withCredentials: true }
    );
  }

  getCommentsForTask(taskId: number): Observable<CommentDTO[]> {
    return this.http.get<CommentDTO[]>(`${this.api}/tasks/${taskId}`, { withCredentials: true });
  }

  addCommentToTask(taskId: number, description: string): Observable<void> {
    return this.http.post<void>(
      `${this.api}/tasks/${taskId}`,
      { description },
      { withCredentials: true }
    );
  }


  deleteComment(taskId: number, id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/task/${taskId}/${id}`, { withCredentials: true });
  }

  updateComment(taskId: number, comment: CommentDTO): Observable<void> {
    return this.http.put<void>(`${this.api}/task/${taskId}/${comment.id}`, comment, { withCredentials: true });
  }

}

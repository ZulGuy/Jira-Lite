import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { UserService } from '../../services/user.service';
import { CommentService } from '../../services/comment.service';
import { TaskDTO, UserDTO, CommentDTO } from '../../types/api.types';
import {AuthService} from "../../services/auth.service";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import Swal from "sweetalert2";

@Component({
  standalone: true,
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class TaskDetailComponent implements OnInit {
  task!: TaskDTO;
  taskId!: number;
  users: UserDTO[] = [];
  comments: CommentDTO[] = [];
  newComment = '';
  canEdit = false;
  projectId: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private userService: UserService,
    private commentService: CommentService,
    public authService: AuthService,
  ) {}

  goBack() {
    if (!this.task?.projectId) {
      console.error('projectId is missing', this.task);
      return;
    }

    this.router.navigate(['/projects', this.task.projectId]);
  }

  ngOnInit(): void {
    this.taskId = +this.route.snapshot.paramMap.get('id')!;
    this.loadTask();
    this.loadUsers();
    this.loadComments();

  }

  loadTask() {
    this.taskService.getTaskById(this.taskId).subscribe(t => {
      console.log('TASK:', t);

      this.task = t;

      this.authService.canEditTask(t.projectId).subscribe(canEdit => {
        this.canEdit = canEdit;
      });
    });
  }


  loadUsers() {
    this.userService.getAll().subscribe(users => this.users = users);
  }

  loadComments() {
    this.commentService.getCommentsForTask(this.taskId).subscribe(data => {
      console.log('Коментарі з сервера:', data);
      this.comments = data;
    });
  }

  save() {
    this.taskService.updateTask(this.task).subscribe({
      next: () => Swal.fire('Збережено!'),
      error: (err) => Swal.fire('Помилка збереження: ' + (err?.error?.message || err?.status || 'невідома помилка'))
    });
  }

  addComment() {
    if (!this.newComment.trim()) return;
    this.commentService.addCommentToTask(this.taskId, this.newComment)
    .subscribe(() => {
      this.newComment = '';
      this.loadComments();
    });
  }

  deleteComment(id: number) {
    if (confirm('Ви впевнені, що хочете видалити цей коментар?')) {
      this.commentService.deleteComment(this.taskId, id).subscribe(() => {
        this.loadComments();
      });
    }
  }

  editComment(comment: CommentDTO) {
    const updated = prompt('Редагувати коментар:', comment.description);
    if (updated !== null) {
      this.commentService.updateComment(this.taskId, { ...comment, description: updated }).subscribe(() => {
        this.loadComments();
      });
    }
  }

}

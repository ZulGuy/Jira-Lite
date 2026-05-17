import { Component, OnInit } from '@angular/core';
import { CdkDragDrop, moveItemInArray, transferArrayItem, DragDropModule } from '@angular/cdk/drag-drop';
import { ProjectService } from '../../services/project.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskDTO, TaskStatus } from '../../types/api.types';
import {Column} from "../../models/column.model";
import {TaskService} from "../../services/task.service";
import {AuthService} from "../../services/auth.service";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {CreateTaskModalComponent} from "../create-task-modal/create-task-modal.component";

@Component({
  standalone: true,
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.scss'],
  imports: [CommonModule, FormsModule, DragDropModule, CreateTaskModalComponent]
})
export class MainViewComponent implements OnInit {
  board = {
    name: '',
    description: '',
    columns: [
      { name: 'TODO', tasks: [] as TaskDTO[] },
      { name: 'IN_PROGRESS', tasks: [] as TaskDTO[] },
      { name: 'DONE', tasks: [] as TaskDTO[] }
    ]
  };

  projectId!: number;

  showCreateTaskModal = false;
  currentUserId: number;

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.projectId = +id;
        this.loadProject();
        this.loadTasks();
      }
    });
    this.currentUserId = this.authService.getCurrentUser()?.id;
  }


  drop(event: CdkDragDrop<TaskDTO[]>, newStatus: TaskStatus) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      return;
    }

    const task = event.previousContainer.data[event.previousIndex];
    const oldStatus = task.status;

    if (newStatus === 'DONE' && oldStatus !== 'IN_PROGRESS') {
      alert('Задачу можна завершити лише зі статусу "IN_PROGRESS"');
      return;
    }

    task.status = newStatus;
    transferArrayItem(event.previousContainer.data, event.container.data,
      event.previousIndex, event.currentIndex);


    this.taskService.updateTask(task).subscribe({
      next: () => {
        if (newStatus === 'DONE') {
          const doneColumn =
            this.board.columns.find(c => c.name === newStatus)
          if (doneColumn) {
            doneColumn.tasks = doneColumn.tasks.filter(t => t.id !== task.id);
          }
        }
      },
      error: () => {
        task.status = oldStatus;
        transferArrayItem(
          event.container.data,
          event.previousContainer.data,
          event.currentIndex,
          event.previousIndex
        );
      }
    });
  }

  openTask(task: TaskDTO) {
    this.router.navigate(['/task', task.id]);
  }

  loadProject() {
    this.projectService.getById(this.projectId).subscribe(project => {
      this.board.name = project.name;
      this.board.description = project.description;
    });
  }

  loadTasks() {
    this.taskService.getTasksForProject(this.projectId).subscribe(tasks => {
      const columnsMap: Record<string, TaskDTO[]> = {
        TODO: [],
        IN_PROGRESS: [],
        DONE: []
      };

      for (const task of tasks) {
        if (task.status === 'DONE') continue;

        const status = task.status || 'TODO';
        if (columnsMap[status]) {
          columnsMap[status].push(task);
        }
      }

      this.board.columns = [
        new Column('TODO', columnsMap.TODO),
        new Column('IN_PROGRESS', columnsMap.IN_PROGRESS),
        new Column('DONE', [])
      ];
    });
  }


  getColumnLabel(name: string): string {
    const labels: Record<string, string> = { TODO: 'To Do', IN_PROGRESS: 'In Progress', DONE: 'Done' };
    return labels[name] || name;
  }

  handleTaskCreated(task: any) {
    this.taskService.createTask(task).subscribe({
      next: (createdTask) => {
        const status = createdTask.status || 'TODO';
        const column =
          this.board.columns.find(c => c.name === status);
        if (column) {
          column.tasks.push(createdTask);
        }
        this.showCreateTaskModal = false;
      },
      error: (err) => console.error('Failed to create task', err)
    });
  }

}

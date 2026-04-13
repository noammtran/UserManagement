import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { UserService, UsersListResponse, UserHistoryItem } from '../../../core/services/user';
import { User } from '../../../models/user.model';
import { UserHistory } from '../user-history/user-history';
import { UserForm } from '../user-form/user-form';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, UserHistory, UserForm],
  templateUrl: './user-list.html',
  styleUrls: ['./user-list.css'],
})
export class UserList implements OnInit {
  users: User[] = [];
  total = 0;
  keyword = '';
  loading = false;
  errorText = '';

  selectedHistoryUser?: User;
  histories: UserHistoryItem[] = [];
  selectedUser?: User;
  showForm = false;

  selectedIds = new Set<string>();
  allSelected = false;
  statusDropdownFor?: string;
  historyPage = 1;
  historyLimit = 10;
  historyPagination?: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
    hasNext: boolean;
    hasPrev: boolean;
  };

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  editUser(user: User): void {
    this.selectedUser = user;
    this.showForm = true;
  }

  createUser(): void {
    this.selectedUser = undefined;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selectedUser = undefined;
    this.loadUsers();
  }

  loadUsers(): void {
    this.errorText = '';
    this.loading = true;
    this.clearSelection();
    this.userService
      .list(this.keyword)
      .pipe(
        finalize(() => {
          this.loading = false;
          Promise.resolve().then(() => this.cdr.detectChanges());
        }),
      )
      .subscribe({
        next: (res: any) => {
          // hỗ trợ cả hai dạng response: {total, items} hoặc {data:{total, items}}
          const data = res?.data ?? res;
          this.users = data?.items ?? [];
          this.total = data?.total ?? 0;
        },
        error: (err) => {
          this.errorText = err?.message || 'Cannot load user list';
        },
      });
  }

  search(): void {
    this.loadUsers();
  }

  toggleSelectAll(checked: boolean): void {
    this.selectedIds.clear();
    if (checked) {
      this.users.forEach((u) => this.selectedIds.add(u.id));
    }
    this.allSelected = checked;
  }

  toggleSelectOne(id: string, checked: boolean): void {
    if (checked) {
      this.selectedIds.add(id);
    } else {
      this.selectedIds.delete(id);
      this.allSelected = false;
    }
    if (this.selectedIds.size === this.users.length && this.users.length > 0) {
      this.allSelected = true;
    }
  }

  isSelected(id: string): boolean {
    return this.selectedIds.has(id);
  }

  activateSelected(): void {
    if (this.selectedIds.size === 0) {
      return;
    }
    this.userService.activateMany(Array.from(this.selectedIds)).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        this.errorText = err?.message || 'Cannot activate selected users';
      },
    });
  }

  deactivateSelected(): void {
    if (this.selectedIds.size === 0) {
      return;
    }
    this.userService.deactivateMany(Array.from(this.selectedIds)).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        this.errorText = err?.message || 'Cannot deactivate selected users';
      },
    });
  }

  deleteSelected(): void {
    if (this.selectedIds.size === 0) {
      return;
    }
    this.userService.deleteMany(Array.from(this.selectedIds)).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        this.errorText = err?.message || 'Cannot delete selected users';
      },
    });
  }

  private clearSelection(): void {
    this.selectedIds.clear();
    this.allSelected = false;
  }

  deleteUser(id: string): void {
    this.userService.deleteOne(id).subscribe(() => this.loadUsers());
  }

  changeStatus(user: User, statusFlag: number): void {
    if (statusFlag === user.statusFlag) {
      return;
    }
    const action$ = statusFlag === 1 ? this.userService.activateOne(user.id) : this.userService.deactivateOne(user.id);
    action$.subscribe({
      next: () => this.loadUsers(),
      error: (err) => {
        this.errorText = err?.message || 'Cannot change user status';
      },
    });
    this.statusDropdownFor = undefined;
  }

  toggleStatusDropdown(userId: string): void {
    this.statusDropdownFor = this.statusDropdownFor === userId ? undefined : userId;
  }

  activateUser(id: string): void {
    this.userService.activateOne(id).subscribe(() => this.loadUsers());
  }

  deactivateUser(id: string): void {
    this.userService.deactivateOne(id).subscribe(() => this.loadUsers());
  }

  showHistory(user: User): void {
    this.selectedHistoryUser = user;
    this.historyPage = 1;
    this.loadHistoryPage();
  }

  loadHistoryPage(page: number = this.historyPage): void {
    if (!this.selectedHistoryUser) {
      return;
    }
    this.historyPage = page;
    this.userService.getHistory(this.selectedHistoryUser.id, this.historyPage, this.historyLimit).subscribe((res) => {
      this.histories = res.data?.items ?? [];
      this.historyPagination = res.data?.pagination;
    });
  }

  nextHistoryPage(): void {
    if (this.historyPagination?.hasNext) {
      this.loadHistoryPage(this.historyPage + 1);
    }
  }

  prevHistoryPage(): void {
    if (this.historyPagination?.hasPrev) {
      this.loadHistoryPage(Math.max(1, this.historyPage - 1));
    }
  }
}

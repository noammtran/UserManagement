import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../../models/user.model';

export interface UsersListResponse {
  total: number;
  items: User[];
}

export interface UserHistoryItem {
  id: string;
  fieldName: string;
  oldValue: string;
  newValue: string;
  changedBy: string;
  changedDate: string;
}

export interface HistoryResponse {
  success: boolean;
  statusCode: number;
  message: string;
  data: {
    items: UserHistoryItem[];
    pagination: {
      page: number;
      pageSize: number;
      totalItems: number;
      totalPages: number;
      hasNext: boolean;
      hasPrev: boolean;
    };
  };
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/user-service/api/v1/users';

  list(keyword?: string): Observable<UsersListResponse> {
    const params: Record<string, string> = {};
    if (keyword) {
      params['fullName'] = keyword;
    }
    return this.http.get<UsersListResponse>(this.baseUrl, { params });
  }

  deleteOne(id: string) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteMany(ids: string[], hardDelete = false) {
    return this.http.request('delete', this.baseUrl, {
      body: { ids, hardDelete },
    });
  }

  activateOne(id: string) {
    return this.http.patch(`${this.baseUrl}/${id}/activate`, {});
  }

  deactivateOne(id: string) {
    return this.http.patch(`${this.baseUrl}/${id}/deactivate`, {});
  }

  activateMany(ids: string[]) {
    return this.http.patch(`${this.baseUrl}/activate`, { ids });
  }

  deactivateMany(ids: string[]) {
    return this.http.patch(`${this.baseUrl}/deactivate`, { ids });
  }

  getHistory(userId: string, page = 1, limit = 20): Observable<HistoryResponse> {
    return this.http.get<HistoryResponse>(`${this.baseUrl}/${userId}/history`, {
      params: { page, limit },
    });
  }

  create(payload: any) {
    return this.http.post(this.baseUrl, payload);
  }

  update(id: string, payload: any) {
    return this.http.put(`${this.baseUrl}/${id}`, payload);
  }
}

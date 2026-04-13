import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LookupValue } from '../../models/lookup-value.model';

export interface DepartmentOption {
  id: number;
  departmentName: string;
}

export interface CompanyOption {
  id: number;
  companyName: string;
  stockCode: string;
  stockExchange: string;
}

export interface MasterDataResponse {
  success: boolean;
  statusCode: number;
  message: string;
  data: {
    genders: LookupValue[];
    positions: LookupValue[];
    identityTypes: LookupValue[];
    userStatuses: LookupValue[];
    departments: DepartmentOption[];
    companies: CompanyOption[];
  };
}

@Injectable({
  providedIn: 'root',
})
export class LookupService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/masterdata_service/api/v1/masterdata';

  getMasterData(): Observable<MasterDataResponse> {
    return this.http.get<MasterDataResponse>(this.baseUrl);
  }
}

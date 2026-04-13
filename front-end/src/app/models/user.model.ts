export interface User {
  id: string;
  userName: string;
  fullName: string;
  email: string;
  statusFlag: number;
  departmentId?: number;
  departmentName?: string;
  positionCode?: number;
  companyProfileId?: number;
  companyName?: string;
  stockCode?: string;
  stockExchange?: string;
  createdBy?: string;
  createdDate?: string;
  lastUpdatedBy?: string;
  lastUpdatedDate?: string;
  phoneNumber?: string;
  faxNumber?: string;
  address?: string;
  birthDate?: string;
  genderCode?: number;
  identityNumber?: string;
  identityTypeCode?: number;
  identityIssuedDate?: string;
  identityIssuedPlace?: string;
  description?: string;
}

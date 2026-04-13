import { Routes } from '@angular/router';
import { UserList } from './features/users/user-list/user-list';

export const routes: Routes = [
  { path: '', redirectTo: 'users', pathMatch: 'full' },
  { path: 'users', component: UserList },
  { path: '**', redirectTo: 'users' }
];

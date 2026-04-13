import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserHistoryItem } from '../../../core/services/user';

@Component({
  selector: 'app-user-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-history.html',
  styleUrls: ['./user-history.css'],
})
export class UserHistory {
  @Input() items: UserHistoryItem[] = [];
}

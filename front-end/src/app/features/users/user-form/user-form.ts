import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { LookupService, MasterDataResponse } from '../../../core/services/lookup';
import { UserService } from '../../../core/services/user';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.css'],
})
export class UserForm implements OnInit, OnChanges {
  @Input() user?: User;
  @Output() saved = new EventEmitter<void>();

  masterData?: MasterDataResponse['data'];
  loading = false;
  error = '';
  success = '';

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private lookupService: LookupService,
    private userService: UserService,
    private fb: FormBuilder,
  ) {
    this.form = this.fb.group({
      userName: ['', [Validators.required, Validators.maxLength(18)]],
      fullName: ['', [Validators.required, Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
      password: ['', [Validators.maxLength(18)]],
      departmentId: [null as number | null, Validators.required],
      positionCode: [null as number | null, Validators.required],
      companyProfileId: [null as number | null, Validators.required],
      statusFlag: [1, Validators.required],
      description: ['', Validators.maxLength(255)],
    });
  }

  get f() {
    return this.form.controls;
  }

  ngOnInit(): void {
    this.loadMasterData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user']) {
      this.patchFromUser();
    }
  }

  private loadMasterData() {
    this.lookupService.getMasterData().subscribe({
      next: (res) => {
        this.masterData = res.data;
      },
      error: () => {
        this.error = 'Cannot load master data';
      },
    });
  }

  private patchFromUser() {
    if (this.user) {
      const statusFlagValue = this.normalizeStatusFlag(this.user.statusFlag);
      this.form.reset({
        userName: this.user.userName,
        fullName: this.user.fullName,
        email: this.user.email,
        departmentId: this.user.departmentId || null,
        positionCode: this.user.positionCode || null,
        companyProfileId: this.user.companyProfileId || null,
        statusFlag: statusFlagValue,
        description: this.user.description || '',
        password: '',
      });
      this.form.get('userName')?.disable();
    } else {
      this.reset();
    }
  }

  reset() {
    this.error = '';
    this.success = '';
    this.form.reset({
      userName: '',
      fullName: '',
      email: '',
      password: '',
      departmentId: null,
      positionCode: null,
      companyProfileId: null,
      statusFlag: 1,
      description: '',
    });
    this.form.get('userName')?.enable();
  }

  submit() {
    this.error = '';
    this.success = '';
    this.loading = true;

    if (this.form.invalid) {
      this.loading = false;
      this.error = 'Please fill all required fields';
      return;
    }

    const formValue = this.form.getRawValue();
    const statusFlag = this.normalizeStatusFlag(formValue.statusFlag);

    if (!this.user && (!formValue.password || !formValue.password.trim())) {
      this.loading = false;
      this.error = 'Password is required for new user';
      this.form.get('password')?.markAsTouched();
      return;
    }

    if (this.user && this.user.id) {
      const payload: any = {
        fullName: formValue.fullName,
        email: formValue.email,
        departmentId: formValue.departmentId,
        positionCode: formValue.positionCode,
        companyProfileId: formValue.companyProfileId,
        statusFlag: statusFlag,
        description: formValue.description,
      };
      if (formValue.password) {
        payload.password = formValue.password;
      }
      this.userService.update(this.user.id, payload).subscribe({
        next: () => {
          this.loading = false;
          this.success = 'Updated successfully';
          this.saved.emit();
        },
        error: (err) => {
          this.loading = false;
          this.error = err?.error?.message || 'Error updating user';
        },
      });
    } else {
      const payload = {
        userName: formValue.userName,
        fullName: formValue.fullName,
        email: formValue.email,
        password: formValue.password,
        departmentId: formValue.departmentId,
        positionCode: formValue.positionCode,
        companyProfileId: formValue.companyProfileId,
        statusFlag: statusFlag,
        description: formValue.description,
      };
      this.userService.create(payload).subscribe({
        next: () => {
          this.loading = false;
          this.success = 'Created successfully';
          this.saved.emit();
          this.reset();
        },
        error: (err) => {
          this.loading = false;
          this.error = err?.error?.message || 'Error creating user';
        },
      });
    }
  }

  private normalizeStatusFlag(value: unknown): number {
    if (typeof value === 'number') {
      return value;
    }
    if (typeof value === 'string') {
      const v = value.toLowerCase();
      if (v.includes('active')) {
        return 1;
      }
      if (v.includes('inactive')) {
        return 0;
      }
      const parsed = Number(value);
      if (!Number.isNaN(parsed)) {
        return parsed;
      }
    }
    return 1;
  }
}

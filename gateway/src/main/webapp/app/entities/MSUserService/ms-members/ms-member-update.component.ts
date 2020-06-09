import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { IMSMember, MSMember } from 'app/shared/model/MSUserService/ms-member.model';
import { MSMemberService } from './ms-member.service';
import { AccountService, Account } from 'app/core';

function parentSalesforceIdConditionallyRequiredValidator(formGroup: FormGroup) {
  if (!formGroup.value.isConsortiumLead) {
    return Validators.required(formGroup.get('parentSalesforceId')) ? { parentSalesforceIdConditionallyRequired: true } : null;
  }

  return null;
}

@Component({
  selector: 'jhi-ms-member-update',
  templateUrl: './ms-member-update.component.html'
})
export class MSMemberUpdateComponent implements OnInit {
  isSaving: boolean;
  editForm = this.fb.group(
    {
      id: [],
      clientId: [null, [Validators.required]],
      clientName: [],
      salesforceId: [null, [Validators.required]],
      parentSalesforceId: [[Validators.required]],
      isConsortiumLead: [null, [Validators.required]],
      assertionServiceEnabled: [],
      createdBy: [],
      createdDate: [],
      lastModifiedBy: [],
      lastModifiedDate: []
    },
    { validators: [parentSalesforceIdConditionallyRequiredValidator] }
  );

  constructor(
    private accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected msMemberService: MSMemberService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.accountService.identity().then((account: Account) => {});
    this.activatedRoute.data.subscribe(({ msMember }) => {
      this.updateForm(msMember);
    });
  }

  updateForm(msMember: IMSMember) {
    this.editForm.patchValue({
      id: msMember.id,
      clientId: msMember.clientId,
      clientName: msMember.clientName,
      salesforceId: msMember.salesforceId,
      parentSalesforceId: msMember.parentSalesforceId,
      isConsortiumLead: msMember.isConsortiumLead,
      assertionServiceEnabled: msMember.assertionServiceEnabled,
      createdBy: msMember.createdBy,
      createdDate: msMember.createdDate != null ? msMember.createdDate.format(DATE_TIME_FORMAT) : null,
      lastModifiedBy: msMember.lastModifiedBy,
      lastModifiedDate: msMember.lastModifiedDate != null ? msMember.lastModifiedDate.format(DATE_TIME_FORMAT) : null
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const msMember = this.createFromForm();
    if (msMember.id !== undefined) {
      this.subscribeToSaveResponse(this.msMemberService.update(msMember));
    } else {
      this.subscribeToSaveResponse(this.msMemberService.create(msMember));
    }
  }

  private createFromForm(): IMSMember {
    return {
      ...new MSMember(),
      id: this.editForm.get(['id']).value,
      clientId: this.editForm.get(['clientId']).value,
      clientName: this.editForm.get(['clientName']).value,
      salesforceId: this.editForm.get(['salesforceId']).value,
      parentSalesforceId: this.editForm.get(['parentSalesforceId']).value,
      isConsortiumLead: this.editForm.get(['isConsortiumLead']).value,
      assertionServiceEnabled: this.editForm.get(['assertionServiceEnabled']).value,
      createdBy: this.editForm.get(['createdBy']).value,
      createdDate:
        this.editForm.get(['createdDate']).value != null ? moment(this.editForm.get(['createdDate']).value, DATE_TIME_FORMAT) : undefined,
      lastModifiedBy: this.editForm.get(['lastModifiedBy']).value,
      lastModifiedDate:
        this.editForm.get(['lastModifiedDate']).value != null
          ? moment(this.editForm.get(['lastModifiedDate']).value, DATE_TIME_FORMAT)
          : undefined
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMSMember>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}

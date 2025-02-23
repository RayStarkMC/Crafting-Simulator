import {Component, computed, inject, signal} from '@angular/core';
import {
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {RegisterItemService} from "../../../backend/register-item.service";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";

export type State =
  |
  Readonly<{
    type: "standing-by"
  }>
  |
  Readonly<{
    type: "sending"
  }>

export type CreateItemDialogRef = MatDialogRef<CreateItemDialogComponent, CreateItemDialogResult>
export type CreateItemDialogResult = "succeeded"

@Component({
  selector: 'create-item-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule
  ],
  templateUrl: './create-item-dialog.component.html',
  styleUrl: './create-item-dialog.component.css'
})
export class CreateItemDialogComponent {
  static open(dialog: MatDialog): CreateItemDialogRef {
    return dialog.open(CreateItemDialogComponent, {
      disableClose: true
    })
  }

  private readonly registerItemService = inject(RegisterItemService)
  private readonly dialogRef = inject<CreateItemDialogRef>(MatDialogRef)

  private readonly state = signal<State>({
    type: "standing-by"
  })
  readonly isSending = computed(() => {
    switch (this.state().type) {
      case "standing-by": {
        return false
      }
      case "sending": {
        return true
      }
    }
  });

  readonly formGroup = new FormGroup({
    name: new FormControl<string>("",
      {
        validators: Validators.required,
      }
    ),
  })

  sendThenCloseDialog(): void {
    if (this.formGroup.invalid) return;
    const formValue = this.formGroup.value
    if (formValue.name === undefined || formValue.name === null) return;

    this.state.set({
      type: "sending"
    })

    this
      .registerItemService
      .run({
        name: formValue.name
      })
      .subscribe({
        next: () => this.dialogRef.close("succeeded")
      })
  }
}

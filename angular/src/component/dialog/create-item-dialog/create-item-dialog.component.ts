import {Component, computed, inject, OnInit, signal} from '@angular/core';
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
    MatDialogClose
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

  sendThenCloseDialog(): void {
    this.state.set({
      type: "sending"
    })
    this
      .registerItemService
      .run({
        name: "wow!!!"
      })
      .subscribe({
        next: () => this.dialogRef.close("succeeded")
      })
  }
}

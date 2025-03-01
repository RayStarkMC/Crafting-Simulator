import {Component, inject, signal} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef, MatDialogTitle
} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {MatButton} from "@angular/material/button";

export type State =
  |
  Readonly<{
    type: "standing-by"
  }>
  |
  Readonly<{
    type: "doing"
  }>

export type WarningDialogRef<T> = MatDialogRef<WarningDialogComponent, WarningDialogResult<T>>
export type WarningDialogData<T> = Readonly<{
  onConfirmed: Observable<T>,
  title: string,
  messageWarning: string,
  messageOnDoing: string,
}>
export type WarningDialogResult<T> = Readonly<{
  confirmed: "confirmed",
  result: T,
}>

@Component({
  selector: 'warning-dialog',
  imports: [
    MatDialogContent,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogTitle
  ],
  templateUrl: './warning-dialog.component.html',
  styleUrl: './warning-dialog.component.css'
})
export class WarningDialogComponent {
  static open<T>(dialog: MatDialog, data?: WarningDialogData<T>): WarningDialogRef<T> {
    return dialog.open(WarningDialogComponent, {
      disableClose: true,
      data: data
    })
  }

  data = inject<WarningDialogData<unknown>>(MAT_DIALOG_DATA)
  private readonly dialogRef = inject<WarningDialogRef<unknown>>(MatDialogRef)

  state = signal<State>({
    type: "standing-by",
  })

  onConfirmed(): void {
    this.state.set({
      type: "doing",
    })

    this.data.onConfirmed.subscribe({
      next: result => this.dialogRef.close({
        confirmed: "confirmed",
        result: result,
      })
    })
  }
}

import {Component} from '@angular/core';
import {
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";

type ItemId = string

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
  static open(dialog: MatDialog): MatDialogRef<CreateItemDialogComponent, ItemId> {
    return dialog.open(CreateItemDialogComponent)
  }

  sendForm(): ItemId {
    return "ID!!!!!!"
  }
}

import {Component, computed, inject, input, signal} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {RegisterItemService} from "../../../backend/register-item.service";
import {Router} from "@angular/router";
import {concatMap, filter} from "rxjs";
import {WarningDialogComponent} from "../../dialog/warning-dialog/warning-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {DeleteItemService} from "../../../backend/delete-item.service";
import {UpdateItemService} from "../../../backend/update-item.service";

export type Mode =
  |
  Readonly<{
    type: "CREATE",
  }>
  |
  Readonly<{
    type: "UPDATE",
    id: string,
    name: string,
  }>

@Component({
  selector: 'edit-item',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel
  ],
  templateUrl: './edit-item.component.html',
  styleUrl: './edit-item.component.css'
})
export class EditItemComponent {
  private readonly registerItemService = inject(RegisterItemService)
  private readonly updateItemService = inject(UpdateItemService)
  private readonly deleteItemsService = inject(DeleteItemService)
  private readonly router = inject(Router)
  private readonly dialog = inject(MatDialog)

  readonly mode = input.required<Mode>()
  readonly sending = signal<boolean>(false)

  readonly formGroup = computed(() => {
    const currentMode = this.mode()
    let name: string | null
    switch (currentMode.type) {
      case "CREATE": {
        name = null
        break
      }
      case "UPDATE": {
        name = currentMode.name
        break
      }
    }

    return new FormGroup({
      name: new FormControl<string | null>(name,
        {
          validators: Validators.required,
        }
      ),
    })
  })

  sendForm(): void {
    if (this.formGroup().invalid) return
    const formValue = this.formGroup().value
    if (formValue.name === undefined || formValue.name === null) return

    this.sending.set(true)

    const currentState = this.mode()

    switch (currentState.type) {
      case "CREATE": {
        this
          .registerItemService
          .run({
            name: formValue.name
          })
          .pipe(
            concatMap(() => this.router.navigateByUrl("/items"))
          )
          .subscribe()
        return
      }
      case "UPDATE": {
        this
          .updateItemService
          .run({
            id: currentState.id,
            name: formValue.name
          })
          .pipe(
            concatMap(() => this.router.navigateByUrl("/items"))
          )
          .subscribe()
        return
      }
    }
  }

  openWarningDialog(id: string): void {
    WarningDialogComponent
      .open(this.dialog, {
        onConfirmed: this.deleteItemsService.run({
          id: id
        }),
        title: "Delete item",
        messageWarning: "This operation cannot be undone.",
        messageOnDoing: "deleting..."
      })
      .afterClosed()
      .pipe(
        filter(result => result?.confirmed === "confirmed"),
        concatMap(() => this.router.navigateByUrl("/items"))
      )
      .subscribe()
  }
}

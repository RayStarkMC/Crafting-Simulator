import {Component, inject, OnInit, signal, TrackByFunction} from '@angular/core';
import {LayoutComponent} from "../../shared/layout/layout.component";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateItemDialogComponent} from "../../dialog/create-item-dialog/create-item-dialog.component";
import {concatMap, delay, filter, map, Observable, of, tap} from "rxjs";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {SearchItemsRequest, SearchItemsService} from "../../../backend/search-items.service";
import {DeleteItemService} from "../../../backend/delete-item.service";
import {WarningDialogComponent} from "../../dialog/warning-dialog/warning-dialog.component";

export type State =
  |
  Readonly<{
    type: "PRE_INITIALIZED",
  }>
  |
  Readonly<{
    type: "INITIALIZED",
    items: TableModel
  }>
export type TableModel = readonly TableRow[]
export type TableRow = Readonly<{
  id: string,
  name: string,
}>


@Component({
  selector: 'items',
  imports: [
    LayoutComponent,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    MatIconButton,
    MatIcon,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
  ],
  templateUrl: './items.component.html',
  styleUrl: './items.component.css'
})
export class ItemsComponent implements OnInit {
  private readonly searchItemsService = inject(SearchItemsService)
  private readonly deleteItemsService = inject(DeleteItemService)
  private readonly dialog = inject(MatDialog)

  readonly formGroup = new FormGroup({
    name: new FormControl<string | null>(null)
  })

  readonly state = signal<State>({
    type: "PRE_INITIALIZED",
  })

  readonly cooldown = signal<boolean>(false)

  ngOnInit(): void {
    this.reloadItems()
  }

  readonly trackTableRowById: TrackByFunction<TableRow> = (_, item) => item.id

  reloadItems(): void {
    of(undefined)
      .pipe(
        tap(() => this.cooldown.set(true)),
        delay(1000),
        tap(() => this.cooldown.set(false))
      )
      .subscribe()

    this.loadItems().subscribe()
  }

  openCreateItemDialog(): void {
    CreateItemDialogComponent
      .open(this.dialog)
      .afterClosed()
      .pipe(
        filter(result => result === "succeeded"),
        concatMap(() => this.loadItems())
      )
      .subscribe()
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
        filter(result => result !== undefined),
        concatMap(() => this.loadItems())
      )
      .subscribe()
  }

  private loadItems(): Observable<void> {
    let request: SearchItemsRequest
    if (this.formGroup.invalid) {
      request = {}
    } else {
      const formValue = this.formGroup.value
      request = {
        name: formValue.name || undefined
      }
    }

    return this
      .searchItemsService
      .run(request)
      .pipe(
        map(response => {
          return {
            type: "INITIALIZED",
            items: response.list
          } as const
        }),
        tap(state => this.state.set(state)),
        map(() => undefined)
      )
  }
}

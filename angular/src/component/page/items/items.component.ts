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
import {GetAllItemsService} from "../../../backend/get-all-items.service";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {CreateItemDialogComponent} from "../../dialog/create-item-dialog/create-item-dialog.component";
import {concatMap, delay, filter, map, Observable, of, tap} from "rxjs";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {SearchItemsRequest, SearchItemsService} from "../../../backend/search-items.service";

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
  private readonly getAllItemsService = inject(GetAllItemsService)
  private readonly searchItemsService = inject(SearchItemsService)
  private readonly dialog = inject(MatDialog)

  readonly formGroup = new FormGroup({
    name: new FormControl<string | null>(null)
  })

  readonly state = signal<State>({
    type: "PRE_INITIALIZED",
  })

  readonly cooldown = signal<boolean>(false)

  setCooldown(): void {
    of(undefined)
      .pipe(
        tap(() => this.cooldown.set(true)),
        delay(1000),
        tap(() => this.cooldown.set(false))
      )
      .subscribe()
  }

  ngOnInit(): void {
    this.setCooldown()
    this.loadItems().subscribe()
  }

  readonly trackTableRowById: TrackByFunction<TableRow> = (_, item) => item.id

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

  reloadItems(): void {
    this.setCooldown()
    this.loadItems().subscribe()
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

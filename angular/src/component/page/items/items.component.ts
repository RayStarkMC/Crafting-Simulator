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
    MatIcon
  ],
  templateUrl: './items.component.html',
  styleUrl: './items.component.css'
})
export class ItemsComponent implements OnInit {
  private readonly getAllItemsService = inject(GetAllItemsService)
  private readonly dialog = inject(MatDialog)

  readonly state = signal<State>({
    type: "PRE_INITIALIZED"
  })

  ngOnInit(): void {
    this
      .getAllItemsService
      .request()
      .subscribe({
        next: response => {
          this.state.set({
            type: "INITIALIZED",
            items: response.list
          })
        }
      })
  }

  readonly trackTableRowById: TrackByFunction<TableRow> = (_, item) => item.id

  openCreateItemDialog(): void {
    CreateItemDialogComponent
      .open(this.dialog)
      .afterClosed()
      .subscribe({
        next: result => {
          if (result !== undefined) {
            alert(result)
          }
        }
      })
  }
}

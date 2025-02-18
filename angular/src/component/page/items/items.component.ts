import {Component, signal} from '@angular/core';
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

type ItemList = readonly Item[]

type Item = {
  id: string,
  name: string,
}

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
    MatRowDef
  ],
  templateUrl: './items.component.html',
  styleUrl: './items.component.css'
})
export class ItemsComponent {
  readonly items = signal<ItemList>([
    {
      id: "1",
      name: "Item1"
    },
    {
      id: "2",
      name: "Item2"
    },
    {
      id: "3",
      name: "Item3"
    },
  ])

  readonly header = ["id", "name"]
}

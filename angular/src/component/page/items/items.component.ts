import {Component, signal} from '@angular/core';
import {LayoutComponent} from "../../shared/layout/layout.component";
import {MatList, MatListItem} from "@angular/material/list";
import {MatDivider} from "@angular/material/divider";

type Item = {
  id: string,
  name: string,
}

@Component({
  selector: 'items',
  imports: [
    LayoutComponent,
    MatList,
    MatListItem,
    MatDivider
  ],
  templateUrl: './items.component.html',
  styleUrl: './items.component.css'
})
export class ItemsComponent {
  readonly items = signal<readonly Item[]>([
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
}

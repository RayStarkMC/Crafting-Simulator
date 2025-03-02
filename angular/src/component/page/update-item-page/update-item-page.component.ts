import {Component, computed, inject, input, OnInit, signal} from '@angular/core';
import {EditItemComponent, Mode} from "../../shared/edit-item/edit-item.component";
import {LayoutComponent} from "../../shared/layout/layout.component";
import {GetItemService} from "../../../backend/get-item.service";

type State =
  |
  Readonly<{
    type: "PRE_INITIALIZED"
  }>
  |
  Readonly<{
    type: "INITIALIZED",
    mode: Mode,
  }>

@Component({
  selector: 'update-item-page',
  imports: [
    EditItemComponent,
    LayoutComponent
  ],
  templateUrl: './update-item-page.component.html',
  styleUrl: './update-item-page.component.css'
})
export class UpdateItemPageComponent implements OnInit {
  private readonly getItemService = inject(GetItemService)

  readonly id = input.required<string>()

  readonly state = signal<State>({
    type: "PRE_INITIALIZED"
  })

  ngOnInit() {
    this
      .getItemService
      .run({
        id: this.id()
      })
      .subscribe({
        next: response => this.state.set({
          type: "INITIALIZED",
          mode: {
            type: "UPDATE",
            id: response.id,
            name: response.name,
          }
        })
      })
  }
}

import {Component, computed, input} from '@angular/core';
import {EditItemComponent, Mode} from "../../shared/edit-item/edit-item.component";
import {LayoutComponent} from "../../shared/layout/layout.component";

@Component({
  selector: 'update-item-page',
  imports: [
    EditItemComponent,
    LayoutComponent
  ],
  templateUrl: './update-item-page.component.html',
  styleUrl: './update-item-page.component.css'
})
export class UpdateItemPageComponent {
  readonly id = input.required<string>()

  readonly modeUpdate = computed<Mode>(() => {
    return {
      type: "UPDATE",
      id: this.id(),
      name: "default name",
    }
  })
}

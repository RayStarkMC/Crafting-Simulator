import {Component} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {LayoutComponent} from "../../shared/layout/layout.component";
import {EditItemComponent, Mode} from "../../shared/edit-item/edit-item.component";

@Component({
  selector: 'new-item-page',
  imports: [
    FormsModule,
    LayoutComponent,
    ReactiveFormsModule,
    EditItemComponent
  ],
  templateUrl: './new-item-page.component.html',
  styleUrl: './new-item-page.component.css'
})
export class NewItemPageComponent {
  readonly modeCreate: Mode = {
    type: "CREATE"
  }
}

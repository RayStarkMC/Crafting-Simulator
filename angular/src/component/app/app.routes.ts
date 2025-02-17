import {Routes} from "@angular/router";
import {IndexComponent} from "../page/index/index.component";
import {ItemsComponent} from "../page/items/items.component";

export const routes: Routes = [
  {
    path: "",
    component: IndexComponent,
  },
  {
    path: "items",
    component: ItemsComponent,
  }
]

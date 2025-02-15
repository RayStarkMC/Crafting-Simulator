import {bootstrapApplication} from '@angular/platform-browser';
import {AppComponent} from './component/app/app.component';
import {appConfig} from "./component/app/app.config";

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

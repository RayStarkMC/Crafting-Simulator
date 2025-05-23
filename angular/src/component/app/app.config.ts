import {provideRouter, withComponentInputBinding} from "@angular/router";
import {ApplicationConfig, provideZoneChangeDetection} from "@angular/core";
import {routes} from "./app.routes";
import {provideHttpClient} from "@angular/common/http";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(),
    provideAnimationsAsync(),
  ]
};

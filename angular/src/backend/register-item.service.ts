import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

type RegisterItemRequest = {
  name: string,
}
type RegisterItemResponse = {}

@Injectable({
  providedIn: 'root'
})
export class RegisterItemService {
  private readonly http = inject(HttpClient)

  run(request: RegisterItemRequest): Observable<RegisterItemResponse> {
    return this.http.post<RegisterItemResponse>(
      "/api/items",
      request
    )
  }
}

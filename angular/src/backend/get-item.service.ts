import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

export type GetItemRequest = Readonly<{
  id: string
}>

export type GetItemResponse = Readonly<{
  id: string,
  name: string,
}>

@Injectable({
  providedIn: 'root'
})
export class GetItemService {
  private readonly http = inject(HttpClient)

  run(request: GetItemRequest): Observable<GetItemResponse> {
    return this.http.get<GetItemResponse>(
      `/api/items/${request.id}`
    )
  }
}

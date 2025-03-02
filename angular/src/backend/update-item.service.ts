import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

export type UpdateItemRequest = {
  id: string,
  name: string,
}
export type UpdateItemResponse = {}

@Injectable({
  providedIn: 'root'
})
export class UpdateItemService {
  private readonly http = inject(HttpClient)

  run(request: UpdateItemRequest): Observable<UpdateItemResponse> {
    return this.http.put<UpdateItemResponse>(
      `/api/items/${request.id}`,
      {
        name: request.name
      }
    )
  }
}

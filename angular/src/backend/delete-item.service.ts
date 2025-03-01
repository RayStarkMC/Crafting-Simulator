import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

type DeleteItemRequest = Readonly<{
  id: string
}>

type DeleteItemResponse = void

@Injectable({
  providedIn: 'root'
})
export class DeleteItemService {
  private readonly http = inject(HttpClient)

  run(request: DeleteItemRequest): Observable<DeleteItemResponse> {
    return this.http.delete<DeleteItemResponse>(
      `/api/items/${request.id}`
    )
  }
}

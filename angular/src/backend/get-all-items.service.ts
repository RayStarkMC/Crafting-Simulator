import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

type GetAllItemsResponse = Readonly<{
  list: readonly Readonly<{
    id: string,
    name: string,
  }>[]
}>

@Injectable({
  providedIn: 'root'
})
export class GetAllItemsService {
  readonly http = inject(HttpClient)

  request(): Observable<GetAllItemsResponse> {
    return this.http.get<GetAllItemsResponse>("/api/items");
  }
}

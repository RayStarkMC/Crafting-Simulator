import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

export type SearchItemsRequest = Readonly<{
  name?: string
}>

export type SearchItemsResponse = Readonly<{
  list: readonly Readonly<{
    id: string,
    name: string,
  }>[]
}>

@Injectable({
  providedIn: 'root'
})
export class SearchItemsService {
  private readonly http = inject(HttpClient)

  run(request: SearchItemsRequest): Observable<SearchItemsResponse> {
    return this.http.post<SearchItemsResponse>(
      `/api/search/items`,
      request
    )
  }
}

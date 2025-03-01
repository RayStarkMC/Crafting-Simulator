import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
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
    let params = new HttpParams()
    if (request.name !== undefined) {
      params = params.set("name", request.name)
    }

    return this.http.get<SearchItemsResponse>(
      `/api/search/items`,
      {
        params: params
      }
    )
  }
}

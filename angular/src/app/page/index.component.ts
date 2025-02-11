import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";

export type Hello = {
  readonly "message": string
}

@Component({
    selector: 'app-index',
    imports: [],
    templateUrl: './index.component.html',
    styleUrl: './index.component.css'
})
export class IndexComponent implements OnInit {
  private httpClient = inject(HttpClient)
  hello: Hello | null = null

  ngOnInit(): void {
    const hello: Hello = {
      message: "Hello!!!!!!!!!!!"
    }

    this.httpClient.post<Hello>('api/hello', hello).subscribe(hello => this.hello = hello)
  }
}

import { TestBed } from '@angular/core/testing';

import { GetItemService } from './get-item.service';

describe('GetItemService', () => {
  let service: GetItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GetItemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

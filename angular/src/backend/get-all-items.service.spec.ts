import { TestBed } from '@angular/core/testing';

import { GetAllItemsService } from './get-all-items.service';

describe('GetAllItemsService', () => {
  let service: GetAllItemsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GetAllItemsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

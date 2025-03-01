import { TestBed } from '@angular/core/testing';

import { SearchItemsService } from './search-items.service';

describe('SearchItemsService', () => {
  let service: SearchItemsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SearchItemsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

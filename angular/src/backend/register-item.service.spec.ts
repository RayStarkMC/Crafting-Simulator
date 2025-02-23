import { TestBed } from '@angular/core/testing';

import { RegisterItemService } from './register-item.service';

describe('RegisterItemService', () => {
  let service: RegisterItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegisterItemService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

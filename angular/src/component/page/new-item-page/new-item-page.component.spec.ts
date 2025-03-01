import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewItemPageComponent } from './new-item-page.component';

describe('NewItemPageComponent', () => {
  let component: NewItemPageComponent;
  let fixture: ComponentFixture<NewItemPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewItemPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewItemPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

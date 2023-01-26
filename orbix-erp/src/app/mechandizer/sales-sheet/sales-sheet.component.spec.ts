import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesSheetComponent } from './sales-sheet.component';

describe('SalesSheetComponent', () => {
  let component: SalesSheetComponent;
  let fixture: ComponentFixture<SalesSheetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesSheetComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

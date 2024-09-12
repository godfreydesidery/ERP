import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NegativeStockReportComponent } from './negative-stock-report.component';

describe('NegativeStockReportComponent', () => {
  let component: NegativeStockReportComponent;
  let fixture: ComponentFixture<NegativeStockReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NegativeStockReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NegativeStockReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

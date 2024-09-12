import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesAndPurchasesSummaryReportComponent } from './sales-and-purchases-summary-report.component';

describe('SalesAndPurchasesSummaryReportComponent', () => {
  let component: SalesAndPurchasesSummaryReportComponent;
  let fixture: ComponentFixture<SalesAndPurchasesSummaryReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesAndPurchasesSummaryReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesAndPurchasesSummaryReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

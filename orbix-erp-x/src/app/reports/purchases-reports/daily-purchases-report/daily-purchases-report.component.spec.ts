import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DailyPurchasesReportComponent } from './daily-purchases-report.component';

describe('DailyPurchasesReportComponent', () => {
  let component: DailyPurchasesReportComponent;
  let fixture: ComponentFixture<DailyPurchasesReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DailyPurchasesReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DailyPurchasesReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

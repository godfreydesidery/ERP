import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplySalesReportExtendedComponent } from './supply-sales-report-extended.component';

describe('SupplySalesReportExtendedComponent', () => {
  let component: SupplySalesReportExtendedComponent;
  let fixture: ComponentFixture<SupplySalesReportExtendedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplySalesReportExtendedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplySalesReportExtendedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterialVsProductionReportComponent } from './material-vs-production-report.component';

describe('MaterialVsProductionReportComponent', () => {
  let component: MaterialVsProductionReportComponent;
  let fixture: ComponentFixture<MaterialVsProductionReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MaterialVsProductionReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MaterialVsProductionReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

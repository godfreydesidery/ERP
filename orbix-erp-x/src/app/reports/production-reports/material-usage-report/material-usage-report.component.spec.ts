import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterialUsageReportComponent } from './material-usage-report.component';

describe('MaterialUsageReportComponent', () => {
  let component: MaterialUsageReportComponent;
  let fixture: ComponentFixture<MaterialUsageReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MaterialUsageReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MaterialUsageReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

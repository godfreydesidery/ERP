import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LpoReportComponent } from './lpo-report.component';

describe('LpoReportComponent', () => {
  let component: LpoReportComponent;
  let fixture: ComponentFixture<LpoReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LpoReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LpoReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

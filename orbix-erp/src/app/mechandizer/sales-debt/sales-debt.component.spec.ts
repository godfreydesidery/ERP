import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesDebtComponent } from './sales-debt.component';

describe('DebtTrackerComponent', () => {
  let component: SalesDebtComponent;
  let fixture: ComponentFixture<SalesDebtComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesDebtComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesDebtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

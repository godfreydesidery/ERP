import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierStockStatusComponent } from './supplier-stock-status.component';

describe('SupplierStockStatusComponent', () => {
  let component: SupplierStockStatusComponent;
  let fixture: ComponentFixture<SupplierStockStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplierStockStatusComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplierStockStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

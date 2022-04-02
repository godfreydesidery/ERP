import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierMassManagerComponent } from './supplier-mass-manager.component';

describe('SupplierMassManagerComponent', () => {
  let component: SupplierMassManagerComponent;
  let fixture: ComponentFixture<SupplierMassManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupplierMassManagerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupplierMassManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

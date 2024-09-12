import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerMassManagerComponent } from './customer-mass-manager.component';

describe('CustomerMassManagerComponent', () => {
  let component: CustomerMassManagerComponent;
  let fixture: ComponentFixture<CustomerMassManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomerMassManagerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerMassManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

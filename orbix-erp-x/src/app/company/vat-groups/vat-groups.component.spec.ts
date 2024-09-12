import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VatGroupsComponent } from './vat-groups.component';

describe('VatGroupsComponent', () => {
  let component: VatGroupsComponent;
  let fixture: ComponentFixture<VatGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VatGroupsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VatGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

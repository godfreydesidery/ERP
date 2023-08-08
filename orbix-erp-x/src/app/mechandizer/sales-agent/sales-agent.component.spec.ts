import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesAgentComponent } from './sales-agent.component';

describe('SalesAgentComponent', () => {
  let component: SalesAgentComponent;
  let fixture: ComponentFixture<SalesAgentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SalesAgentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesAgentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

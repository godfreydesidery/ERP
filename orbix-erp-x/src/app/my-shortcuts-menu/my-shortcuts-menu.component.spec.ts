import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyShortcutsMenuComponent } from './my-shortcuts-menu.component';

describe('MyShortcutsMenuComponent', () => {
  let component: MyShortcutsMenuComponent;
  let fixture: ComponentFixture<MyShortcutsMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyShortcutsMenuComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyShortcutsMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SalesExpensePageRoutingModule } from './sales-expense-routing.module';

import { SalesExpensePage } from './sales-expense.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SalesExpensePageRoutingModule
  ],
  declarations: [SalesExpensePage]
})
export class SalesExpensePageModule {}

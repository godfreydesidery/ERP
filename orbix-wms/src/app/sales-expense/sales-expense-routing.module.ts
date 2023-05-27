import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SalesExpensePage } from './sales-expense.page';

const routes: Routes = [
  {
    path: '',
    component: SalesExpensePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SalesExpensePageRoutingModule {}

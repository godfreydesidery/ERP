import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SalesSheetPage } from './sales-sheet.page';

const routes: Routes = [
  {
    path: '',
    component: SalesSheetPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SalesSheetPageRoutingModule {}

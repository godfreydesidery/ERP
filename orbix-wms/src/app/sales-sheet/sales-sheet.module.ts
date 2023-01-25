import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SalesSheetPageRoutingModule } from './sales-sheet-routing.module';

import { SalesSheetPage } from './sales-sheet.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SalesSheetPageRoutingModule
  ],
  declarations: [SalesSheetPage]
})
export class SalesSheetPageModule {}

import { CommonModule, DatePipe } from '@angular/common';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { JwtInterceptor, JwtHelperService, JWT_OPTIONS } from '@auth0/angular-jwt';
import { HttpErrorInterceptor } from 'src/error-interceptor';
import { MatAutocompleteModule} from '@angular/material/autocomplete'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MDBBootstrapModule } from 'angular-bootstrap-md';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { DashboardComponent } from './home/dashboard/dashboard.component';
import { LoginComponent } from './login/login.component';
import { AdminMenuComponent } from './menu/admin-menu/admin-menu.component';
import { SideBarComponent } from './side-bar/side-bar.component';
import { UserProfileComponent } from './user/user-profile/user-profile.component';
import { RoleManagerComponent } from './user/role-manager/role-manager.component';
import { RouterModule } from '@angular/router';
import { AccessContolComponent } from './user/access-contol/access-contol.component';
import { InventoryMenuComponent } from './menu/inventory-menu/inventory-menu.component';
import { ProductMasterComponent } from './inventory/product-master/product-master.component';
import { ProductInquiryComponent } from './inventory/product-inquiry/product-inquiry.component';
import { DepartmentComponent } from './inventory/department/department.component';
import { ClassComponent } from './inventory/class/class.component';
import { SubClassComponent } from './inventory/sub-class/sub-class.component';
import { MaterialMasterComponent } from './inventory/material-master/material-master.component';
import { CategoryComponent } from './inventory/category/category.component';
import { SubCategoryComponent } from './inventory/sub-category/sub-category.component';
import { MechandizerMenuComponent } from './menu/mechandizer-menu/mechandizer-menu.component';
import { SupplierRelationsMenuComponent } from './menu/supplier-relations-menu/supplier-relations-menu.component';
import { CustomerRelationsMenuComponent } from './menu/customer-relations-menu/customer-relations-menu.component';
import { HumanResourceMenuComponent } from './menu/human-resource-menu/human-resource-menu.component';
import { MaterialInquiryComponent } from './inventory/material-inquiry/material-inquiry.component';
import { BiometricsComponent } from './user/biometrics/biometrics.component';
import { TillAdministrationComponent } from './till/till-administration/till-administration.component';
import { CompanyProfileComponent } from './company/company-profile/company-profile.component';
import { ProductMassManagerComponent } from './inventory/product-mass-manager/product-mass-manager.component';
import { MaterialMassManagerComponent } from './inventory/material-mass-manager/material-mass-manager.component';
import { ReportMenuComponent } from './menu/report-menu/report-menu.component';
import { SalesReportsSubMenuComponent } from './reports/sales-reports-sub-menu/sales-reports-sub-menu.component';
import { InventoryReportsSubMenuComponent } from './reports/inventory-reports-sub-menu/inventory-reports-sub-menu.component';
import { ProductionReportsSubMenuComponent } from './reports/production-reports-sub-menu/production-reports-sub-menu.component';
import { LpoComponent } from './mechandizer/lpo/lpo.component';
import { GrnComponent } from './mechandizer/grn/grn.component';
import { QuotationComponent } from './mechandizer/quotation/quotation.component';
import { SalesInvoiceComponent } from './mechandizer/sales-invoice/sales-invoice.component';
import { SalesReceiptComponent } from './mechandizer/sales-receipt/sales-receipt.component';
import { SalesLedgeComponent } from './mechandizer/sales-ledge/sales-ledge.component';
import { SalesJournalComponent } from './mechandizer/sales-journal/sales-journal.component';
import { BillReprintComponent } from './mechandizer/bill-reprint/bill-reprint.component';
import { PackingListComponent } from './mechandizer/packing-list/packing-list.component';
import { CustomerReturnComponent } from './mechandizer/customer-return/customer-return.component';
import { CustomerClaimComponent } from './mechandizer/customer-claim/customer-claim.component';
import { ReturnToVendorComponent } from './mechandizer/return-to-vendor/return-to-vendor.component';
import { VendorCreditNoteComponent } from './mechandizer/vendor-credit-note/vendor-credit-note.component';
import { CustomerCreditNoteComponent } from './mechandizer/customer-credit-note/customer-credit-note.component';
import { AllocationComponent } from './mechandizer/allocation/allocation.component';
import { SupplierMasterComponent } from './supplier/supplier-master/supplier-master.component';
import { CustomerMasterComponent } from './customer/customer-master/customer-master.component';
import { EmployeeRegisterComponent } from './human-resource/employee-register/employee-register.component';
import { GroupLevel1Component } from './inventory/group-level1/group-level1.component';
import { GroupLevel2Component } from './inventory/group-level2/group-level2.component';
import { GroupLevel3Component } from './inventory/group-level3/group-level3.component';
import { GroupLevel4Component } from './inventory/group-level4/group-level4.component';
import { DayMenuComponent } from './menu/day-menu/day-menu.component';
import { EndDayComponent } from './day/end-day/end-day.component';
import { CustomDateComponent } from './day/custom-date/custom-date.component';
import { ProductionMenuComponent } from './menu/production-menu/production-menu.component';
import { CustomProductionComponent } from './production/custom-production/custom-production.component';
import { BatchProductionComponent } from './production/batch-production/batch-production.component';
import { ProductMaterialRatioComponent } from './inventory/product-material-ratio/product-material-ratio.component';
import { ProductToMaterialComponent } from './inventory/product-to-material/product-to-material.component';
import { MaterialToProductComponent } from './inventory/material-to-product/material-to-product.component';
import { ProductToProductComponent } from './inventory/product-to-product/product-to-product.component';
import { MaterialToMaterialComponent } from './inventory/material-to-material/material-to-material.component';
import { NgxSpinnerModule } from "ngx-spinner";
import { DebtReceiptComponent } from './mechandizer/debt-receipt/debt-receipt.component';
import { DebtAllocationComponent } from './mechandizer/debt-allocation/debt-allocation.component';
import { SalesDebtComponent } from './mechandizer/sales-debt/sales-debt.component';
import { DailySalesReportComponent } from './reports/sales-reports/daily-sales-report/daily-sales-report.component';
import { ProductListingReportComponent } from './reports/sales-reports/product-listing-report/product-listing-report.component';
import { SupplySalesReportComponent } from './reports/sales-reports/supply-sales-report/supply-sales-report.component';
import { ZHistoryComponent } from './reports/sales-reports/z-history/z-history.component';
import { FastMovingItemsComponent } from './reports/sales-reports/fast-moving-items/fast-moving-items.component';
import { SlowMovingItemsComponent } from './reports/sales-reports/slow-moving-items/slow-moving-items.component';
import { ProductionReportComponent } from './reports/production-reports/production-report/production-report.component';
import { SalesListComponent } from './mechandizer/sales-list/sales-list.component';
import { StockCardReportComponent } from './reports/inventory-reports/stock-card-report/stock-card-report.component';
import { PageComponent } from './page/page.component';
import { DailySummaryReportComponent } from './reports/sales-reports/daily-summary-report/daily-summary-report.component';
import { NegativeStockReportComponent } from './reports/inventory-reports/negative-stock-report/negative-stock-report.component';
import { SupplierStockStatusComponent } from './reports/inventory-reports/supplier-stock-status/supplier-stock-status.component';
import { SupplierMassManagerComponent } from './supplier/supplier-mass-manager/supplier-mass-manager.component';
import { MyShortcutsMenuComponent } from './my-shortcuts-menu/my-shortcuts-menu.component';
import { DailyPurchasesReportComponent } from './reports/purchases-reports/daily-purchases-report/daily-purchases-report.component';
import { LpoReportComponent } from './reports/purchases-reports/lpo-report/lpo-report.component';
import { GrnReportComponent } from './reports/purchases-reports/grn-report/grn-report.component';
import { DrpComponent } from './mechandizer/drp/drp.component';
import { DailyProductionReportComponent } from './reports/production-reports/daily-production-report/daily-production-report.component';
import { MaterialVsProductionReportComponent } from './reports/production-reports/material-vs-production-report/material-vs-production-report.component';
import { MaterialUsageReportComponent } from './reports/production-reports/material-usage-report/material-usage-report.component';
import { SalesAndPurchasesSummaryReportComponent } from './reports/sales-reports/sales-and-purchases-summary-report/sales-and-purchases-summary-report.component';
import { VatGroupsComponent } from './company/vat-groups/vat-groups.component';
import { SalesAgentComponent } from './mechandizer/sales-agent/sales-agent.component';
import { DebtTrackerComponent } from './mechandizer/debt-tracker/debt-tracker.component';
import { CustomerMassManagerComponent } from './customer/customer-mass-manager/customer-mass-manager.component';
import { SupplySalesReportExtendedComponent } from './reports/sales-reports/supply-sales-report-extended/supply-sales-report-extended.component';
import { SalesSheetComponent } from './mechandizer/sales-sheet/sales-sheet.component';
import { WebPosComponent } from './mechandizer/web-pos/web-pos.component';


@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    HeaderComponent,
    DashboardComponent,
    LoginComponent,
    AdminMenuComponent,
    SideBarComponent,
    UserProfileComponent,
    RoleManagerComponent,
    AccessContolComponent,
    InventoryMenuComponent,
    ProductMasterComponent,
    ProductInquiryComponent,
    DepartmentComponent,
    ClassComponent,
    SubClassComponent,
    MaterialMasterComponent,
    CategoryComponent,
    SubCategoryComponent,
    MechandizerMenuComponent,
    SupplierRelationsMenuComponent,
    CustomerRelationsMenuComponent,
    HumanResourceMenuComponent,
    MaterialInquiryComponent,
    BiometricsComponent,
    TillAdministrationComponent,
    CompanyProfileComponent,
    ProductMassManagerComponent,
    MaterialMassManagerComponent,
    WebPosComponent,
    ReportMenuComponent,
    SalesReportsSubMenuComponent,
    InventoryReportsSubMenuComponent,
    ProductionReportsSubMenuComponent,
    LpoComponent,
    GrnComponent,
    QuotationComponent,
    SalesInvoiceComponent,
    SalesReceiptComponent,
    SalesLedgeComponent,
    SalesJournalComponent,
    BillReprintComponent,
    PackingListComponent,
    CustomerReturnComponent,
    CustomerClaimComponent,
    ReturnToVendorComponent,
    VendorCreditNoteComponent,
    CustomerCreditNoteComponent,
    AllocationComponent,
    SupplierMasterComponent,
    CustomerMasterComponent,
    EmployeeRegisterComponent,
    GroupLevel1Component,
    GroupLevel2Component,
    GroupLevel3Component,
    GroupLevel4Component,
    DayMenuComponent,
    EndDayComponent,
    CustomDateComponent,
    ProductionMenuComponent,
    CustomProductionComponent,
    BatchProductionComponent,
    ProductMaterialRatioComponent,
    ProductToMaterialComponent,
    MaterialToProductComponent,
    ProductToProductComponent,
    MaterialToMaterialComponent,
    DebtReceiptComponent,
    DebtAllocationComponent,
    SalesDebtComponent,
    DailySalesReportComponent,
    ProductListingReportComponent,
    SupplySalesReportComponent,
    ZHistoryComponent,
    FastMovingItemsComponent,
    SlowMovingItemsComponent,
    ProductionReportComponent,
    SalesListComponent,
    StockCardReportComponent,
    PageComponent,
    DailySummaryReportComponent,
    NegativeStockReportComponent,
    SupplierStockStatusComponent,
    SupplierMassManagerComponent,
    MyShortcutsMenuComponent,
    DailyPurchasesReportComponent,
    LpoReportComponent,
    GrnReportComponent,
    DrpComponent,
    DailyProductionReportComponent,
    MaterialVsProductionReportComponent,
    MaterialUsageReportComponent,
    SalesAndPurchasesSummaryReportComponent,
    VatGroupsComponent,
    SalesAgentComponent,
    DebtTrackerComponent,
    CustomerMassManagerComponent,
    SupplySalesReportExtendedComponent,
    SalesSheetComponent,
    //BatchProductionComponent,
    //CustomProductionComponent,
  ],
  exports: [ UserProfileComponent ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule,
    FormsModule,
    NgxSpinnerModule,
    MDBBootstrapModule.forRoot(),
    RouterModule.forRoot([
      {path : '', component : DashboardComponent},
      {path : 'home', component : DashboardComponent},
      {path : 'user-profile', component :UserProfileComponent},
      {path : 'role-manager', component :RoleManagerComponent},
      {path : 'access-control', component :AccessContolComponent},
      {path : 'boimetrics', component :BiometricsComponent},
      {path : 'till-administration', component :TillAdministrationComponent},
      {path : 'company-profile', component :CompanyProfileComponent},
      {path : 'product-master', component :ProductMasterComponent},
      {path : 'product-inquiry', component :ProductInquiryComponent},
      {path : 'department', component :DepartmentComponent},
      {path : 'class', component :ClassComponent},
      {path : 'sub-class', component :SubClassComponent},
      {path : 'material-master', component :MaterialMasterComponent},
      {path : 'material-inquiry', component :MaterialInquiryComponent},
      {path : 'category', component :CategoryComponent},
      {path : 'sub-category', component :SubCategoryComponent},
      {path : 'product-mass-manager', component :ProductMassManagerComponent},
      {path : 'material-mass-manager', component :MaterialMassManagerComponent},
      {path : 'sales-reports-sub-menu', component :SalesReportsSubMenuComponent},
      {path : 'inventory-reports-sub-menu', component :InventoryReportsSubMenuComponent},
      {path : 'production-reports-sub-menu', component :ProductionReportsSubMenuComponent},
      {path : 'drp', component: DrpComponent},
      {path : 'lpo', component: LpoComponent},
      {path : 'grn', component: GrnComponent},
      {path : 'quotations', component: QuotationComponent},
      {path : 'sales-invoices', component: SalesInvoiceComponent},
      {path : 'sales-receipts', component: SalesReceiptComponent},
      {path : 'allocations', component: AllocationComponent},
      {path : 'web-pos', component: WebPosComponent},
      {path : 'sales-ledge', component: SalesLedgeComponent},
      {path : 'sales-journal', component: SalesJournalComponent},
      {path : 'bill-reprint', component: BillReprintComponent},
      {path : 'packing-list', component: PackingListComponent},
      {path : 'sales-list', component: SalesListComponent},
      {path : 'customer-returns', component: CustomerReturnComponent},
      {path : 'customer-claims', component: CustomerClaimComponent},
      {path : 'return-to-vendor', component: ReturnToVendorComponent},
      {path : 'vendor-cr-note', component: VendorCreditNoteComponent},
      {path : 'customer-cr-note', component: CustomerCreditNoteComponent},
      {path : 'supplier-master', component: SupplierMasterComponent},
      {path : 'supplier-mass-manager', component: SupplierMassManagerComponent},
      {path : 'customer-master', component: CustomerMasterComponent},
      {path : 'customer-mass-manager', component: CustomerMassManagerComponent},
      {path : 'employee-register', component: EmployeeRegisterComponent},
      {path : 'group-level1', component: GroupLevel1Component},
      {path : 'group-level2', component: GroupLevel2Component},
      {path : 'group-level3', component: GroupLevel3Component},
      {path : 'group-level4', component: GroupLevel4Component},
      {path : 'end-day', component: EndDayComponent},
      {path : 'custom-date', component:CustomDateComponent},
      {path : 'batch-production', component:BatchProductionComponent},
      {path : 'custom-production', component:CustomProductionComponent},
      {path : 'product-material-ratio', component:ProductMaterialRatioComponent},
      {path : 'product-to-material', component:ProductToMaterialComponent},
      {path : 'material-to-product', component:MaterialToProductComponent},
      {path : 'product-to-product', component:ProductToProductComponent},
      {path : 'material-to-material', component:MaterialToMaterialComponent},
      {path : 'debt-receipts', component: DebtReceiptComponent},
      {path : 'debt-allocations', component: DebtAllocationComponent},
      {path : 'sales-debt', component: SalesDebtComponent},
      {path : 'daily-sales-report', component: DailySalesReportComponent},
      {path : 'daily-purchases-report', component: DailyPurchasesReportComponent},
      {path : 'daily-summary-report', component: DailySummaryReportComponent},
      {path : 'z-history', component: ZHistoryComponent},
      {path : 'product-listing-report', component: ProductListingReportComponent},
      {path : 'supply-sales-report', component: SupplySalesReportComponent},
      {path : 'supply-sales-report-extended', component: SupplySalesReportExtendedComponent},
      {path : 'fast-moving-items', component: FastMovingItemsComponent},
      {path : 'slow-moving-items', component: SlowMovingItemsComponent},
      {path : 'production-report', component: ProductionReportComponent},
      {path : 'stock-card-report', component: StockCardReportComponent},
      {path : 'negative-stock-report', component: NegativeStockReportComponent},
      {path : 'supplier-stock-status', component: SupplierStockStatusComponent},
      {path : 'lpo-report', component: LpoReportComponent},
      {path : 'grn-report', component: GrnReportComponent},
      {path : 'daily-production-report', component: DailyProductionReportComponent},
      {path : 'material-vs-production-report', component: MaterialVsProductionReportComponent},
      {path : 'material-usage-report', component: MaterialUsageReportComponent},
      {path : 'sales-and-purchases-summary-report', component: SalesAndPurchasesSummaryReportComponent},
      {path : 'vat-group', component: VatGroupsComponent},
      {path : 'sales-agents', component: SalesAgentComponent},
      {path : 'debt-tracker', component: DebtTrackerComponent},
    ]),
    BrowserAnimationsModule,
  ],
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
  providers: [
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    DatePipe,
    JwtHelperService,
   // { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
   // { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

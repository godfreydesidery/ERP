import { Component, ComponentRef, HostListener, OnDestroy, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { IonModal, PopoverController } from '@ionic/angular';
import { OverlayEventDetail } from '@ionic/core/components';
import { ToastController } from '@ionic/angular';

import { environment } from '../../environments/environment';
import { AuthService } from '../auth.service';

import { NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';

import { ModalController, NavParams } from '@ionic/angular';

import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';

const API_URL = environment.apiUrl;


@Component({
  selector: 'app-sales-sheet',
  templateUrl: './sales-sheet.page.html',
  styleUrls: ['./sales-sheet.page.scss'],
})
export class SalesSheetPage implements OnInit {

  salesSheet: SalesSheetModel | undefined 

  salesSheetSales : SalesSheetSaleModel[] = []

  salesExpenses : SalesExpenseModel[] = []

  totalExpenses : number = 0


  totalSales : number = 0
  totalPaid : number = 0
  totalDiscount : number = 0
  totalCharges : number = 0
  totalDue : number = 0

  constructor(private router : Router,
    public modalCtrl: ModalController,
    private modalService: NgbModal,
    public popoverController: PopoverController,
    private toastController: ToastController,
    private http : HttpClient,
    private spinner : NgxSpinnerService) { }

  ngOnInit() {
    this.getSalesSheet()
  }

  clearValues(){
    this.totalSales  = 0
    this.totalPaid  = 0
    this.totalDiscount  = 0
    this.totalCharges  = 0
    this.totalDue  = 0
  }


  async getSalesSheet(): Promise<void> {
    this.salesSheetSales = []
    this.clearValues()
    let options = {
      //headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    
    if(localStorage.getItem('active-list') == null){
      const toast = await this.toastController.create({
        message: 'No active sales list selected',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
      this.router.navigate(['home'])
      location.reload()
    }
    this.spinner.show()
    await this.http.get<SalesSheetModel>(API_URL+'/wms_load_sales_sheet?sales_list_no='+localStorage.getItem('active-list'), options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        let sales : SalesSheetSaleModel[] = data!.salesSheetSales
        sales.forEach(element => {
          this.salesSheetSales.push(element)
          this.totalSales  = this.totalSales + element.totalAmount
          this.totalPaid  = this.totalPaid + element.totalPaid
          this.totalDiscount  = this.totalDiscount + element.totalDiscount
          this.totalCharges  = this.totalCharges + element.totalCharges
          this.totalDue  = this.totalDue + element.totalDue
        })
        this.getSalesExpenses()
      }
    )
    .catch(
      error => {
        console.log(error)
        alert('could not load  sales sheet')
        this.router.navigate(['home'])
        location.reload()
      }
    )
  }

  async getSalesExpenses(): Promise<void> {
    this.salesExpenses = []
    this.totalExpenses = 0
    let options = {
      //headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    
    if(localStorage.getItem('active-list') == null){
      const toast = await this.toastController.create({
        message: 'No active sales list selected',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
      this.router.navigate(['home'])
      location.reload()
    }
    this.spinner.show()
    await this.http.get<SalesExpenseModel[]>(API_URL+'/wms_load_sales_expenses?sales_list_no='+localStorage.getItem('active-list'), options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        let expenses : SalesExpenseModel[] = data!
        expenses.forEach(element => {
          this.salesExpenses.push(element)
          this.totalExpenses  = this.totalExpenses + (element.amount)
        })
      }
    )
    .catch(
      error => {
        console.log(error)
      }
    )
  }
}

export interface SalesSheetModel{
  id : any
  no : string
  salesSheetSales : SalesSheetSaleModel[]
}

export interface SalesSheetSaleModel{
  id : any
  no : string
  customerName : string
  customerMobile : string
  customerLocation : string
  totalAmount : number
  totalPaid : number
  totalDiscount : number
  totalCharges : number
  totalDue : number
  salesSheetSaleDetails : SalesSheetSaleDetailModel[]
}

export interface SalesSheetSaleDetailModel{
  id : any
  code : string
  barcode : string
  description : string
  qty : number
  price : number
}

export interface SalesExpenseModel{
  id : any
  amount : number
  salesListNo      : string
}

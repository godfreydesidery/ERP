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
  selector: 'app-sales-list',
  templateUrl: './sales-list.page.html',
  styleUrls: ['./sales-list.page.scss'],
})
export class SalesListPage implements OnInit {

  salesList: SalesListModel | undefined 

  salesListDetails : SalesListDetailModel[] = []

  totalPacked : number = 0
  totalQty : number = 0

  constructor(private router : Router,
    public modalCtrl: ModalController,
    private modalService: NgbModal,
    public popoverController: PopoverController,
    private toastController: ToastController,
    private http : HttpClient,
    private spinner : NgxSpinnerService) { }

  ngOnInit() {
    this.getSalesList()
  }

  clearValues(){

    this.totalPacked  = 0
    this.totalQty = 0
  }


  async getSalesList(): Promise<void> {
    this.salesListDetails = []
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
    await this.http.get<SalesListModel>(API_URL+'/wms_load_sales_list?sales_list_no='+localStorage.getItem('active-list'), options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        let sales : SalesListDetailModel[] = data!.salesListDetails
        sales.forEach(element => {
          this.salesListDetails.push(element)
          this.totalPacked  = this.totalPacked + (element.sellingPriceVatIncl * element.totalPacked)
          this.totalQty  = this.totalQty + element.totalPacked
        })
      }
    )
    .catch(
      error => {
        console.log(error)
        alert('could not load  sales list')
        this.router.navigate(['home'])
        location.reload()
      }
    )
  }
}

export interface SalesListModel{
  id : any
  no : string
  totalPacked : number
  salesListDetails : SalesListDetailModel[]
}

export interface SalesListDetailModel{
  id : any
  code : string
  barcode : string
  description : string
  totalPacked : number
  sellingPriceVatIncl : number
}
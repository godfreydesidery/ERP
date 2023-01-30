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
  selector: 'app-sale',
  templateUrl: './sale.page.html',
  styleUrls: ['./sale.page.scss'],
})
export class SalePage implements OnInit {

  @ViewChild('popover') popover : any;
  isOpen = false;

  @ViewChild('popover') productPopover : any;
  isProductOpen = false;


  @ViewChild(IonModal)
  modal!: IonModal;
  message = '';
  name: string = '';


  recordMode : string = 'add'

  


  descriptions : string[]
  availableProducts : LProductModel[] = []
  productList : LProduct[] = []
  currentProduct! : LProductModel

  productId : any = ''
  barcode : string = ''
  code : string = ''
  description : string = ''
  sellingPriceVatIncl : number = 0
  available : number = 0
  qty : number = 0

  totalQty : number = 0
  totalAmount : number = 0
  amountPaid : number = 0
  amountDue : number = 0
  discount : number = 0


  product! : LProduct

  customerName : string = 'GENERAL CUSTOMER'
  customerMobile : string = ''
  customerLocation : string = ''



  no : string

  closeResult = '';

  currentModal : any = null

  constructor(private router : Router,
    public modalCtrl: ModalController,
    private modalService: NgbModal,
    public popoverController: PopoverController,
    private toastController: ToastController,
    private http : HttpClient,
    private spinner: NgxSpinnerService) {
      this.descriptions = []
      this.no = localStorage.getItem('active-list')?.toString()!+this.randomString(10, '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
    }

  ngOnInit() {
   this.loadSalesList()
   this.refreshSummary()
  }


  

  presentPopover(e: Event) {
    this.popover.event = e;
    this.isOpen = true;
  }

  dismissPopover(e : Event){
    this.popover.event = e;
    this.isOpen = false;
  }

  presentCustomerPopover(e: Event) {
    this.popover.event = e;
    this.isOpen = true;
  }

  dismissCustomerPopover(e : Event){
    this.popover.event = e;
    this.isOpen = false;
  }

  presentEditPopover(e: Event, id : any) {
    this.productPopover.event = e;
    this.isProductOpen = true;
    this.clearProduct()
    this.productList.forEach(element => {
      if(id === element.id){
        this.productId = element.id
        this.description = element.description
        this.available = element.available
        this.qty = element.qty
      }
    })
  }

  confirmEditPopover(e : Event, id : any){
    this.popover.event = e;
    this.productList.forEach(element => {
      if(element.id == this.productId){
        if(!isNaN(this.qty)){
          if(this.qty > this.available){
            alert('Insufficient qty')
            this.clearProduct()
            this.isProductOpen = false
            return
          }
          if(this.qty <= 0){
            alert('Invalid qty, zero is not allowed')
            this.clearProduct()
            this.isProductOpen = false
            return
          }
          element.qty = this.qty
          this.isProductOpen = false;
          this.clearProduct()
          return
        }else{
          alert('Invalid entry, qty must be a number')
        }
      }
    })
    this.isProductOpen = false;
  }

  

  randomString(length : number, chars : any) {
    var result = '';
    for (var i = length; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
    return result;
  }

  cancel() {
    this.modal.dismiss(null, 'cancel')
  }

  confirm() {
    this.modal.dismiss(this.name, 'confirm')
  }

  onWillDismiss(event: Event) {
    const ev = event as CustomEvent<OverlayEventDetail<string>>;
    if (ev.detail.role === 'confirm') {
      this.message = `Confirmed, ${ev.detail.data}!`;
    }
  }

  editQty(){

  }



  async loadSalesList(){
    if(localStorage.getItem('active-list') == null){
      this.router.navigate([''], {replaceUrl : true})
      const toast = await this.toastController.create({
        message: 'Could not load sales list. Please activate a sales list to proceed!',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
    }else{
      /**
       * Load sales list and customers data
       */
      /**
       * Loading sales list data
       */
      
      var salesListNo = localStorage.getItem('active-list')

      let agent : {
        salesAgentId : number
      } = JSON.parse(localStorage.getItem('current-user')!)
      var agentId = agent.salesAgentId
      await this.getSalesList(salesListNo, agentId)
    }
  }

  async getSalesList(no: any, agentId : number): Promise<void> {
    this.descriptions = []
    this.availableProducts = []
    let options = {
      //headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<LProductModel[]>(API_URL+'/wms_load_available_products?sales_list_no='+no +'&sales_agent_id='+agentId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data!.forEach(element => {
          this.availableProducts.push(element)
        });
        this.availableProducts.forEach(element => {
          this.descriptions.push(element.description)
        })

      }
    )
    .catch(
      error => {
        console.log(error)
        alert('could not load details')
      }
    )
  }

  searchByDescription(descr : string){
    this.currentProduct!
    this.availableProducts.forEach(element => {
      if(descr == element.description){
        this.currentProduct = element
        this.productId = this.currentProduct.id
        this.barcode = this.currentProduct.barcode
        this.code = this.currentProduct.code
        this.description = this.currentProduct.description
        this.sellingPriceVatIncl = this.currentProduct.price
        this.available = this.currentProduct.available
        return
      }
    })
  }

  async increment(){
    if(this.qty < this.available){
      this.qty = this.qty + 1
    }else{
      /**
       * Display error message
       */
       const toast = await this.toastController.create({
        message: 'Insufficient qty',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
    }
    
  }

  decrement(){
    this.qty = this.qty - 1
    if(this.qty < 0){
      this.qty = 0
    }
  }


  async refreshQty(){
    if(this.qty < 0){
      this.qty = 0
    }
    if(this.qty > this.available){
      this.qty = 0
      /**
       * Display error message
       */
       const toast = await this.toastController.create({
        message: 'Insufficient qty',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
    }
  }

  clearProduct(){
    this.productId = ''
    this.barcode = ''
    this.code = ''
    this.description = ''
    this.sellingPriceVatIncl = 0
    this.available = 0
    this.qty = 0
    this.refreshSummary()
  }

  async addEditProduct(){


    var present : boolean = false

    if(this.qty <= 0){
      /**
       * Display error message, invalid qty value 
       */
      alert('Whoops! Invalid qty value')
      return
    }

    var product : LProduct = {
      id: this.productId,
      barcode: this.barcode,
      code: this.code,
      description: this.description,
      sellingPriceVatIncl: this.sellingPriceVatIncl,
      qty: this.qty,
      available : this.available
    }

    this.productList.forEach(element => {
      if(element.id == product.id){       
        present = true
      }
      if(present == true && this.recordMode == 'edit'){
        /**
         * Edit qty
         */
        element.qty = product.qty
        this.refreshSummary()
        return  
      }else if(present == true && this.recordMode == 'add'){
        /**
         * Adding an already present product, not allowed
         */
        alert('Product already present, consider editing qty')
        this.refreshSummary()
        return
      }
    })
    if(present == false){
      this.productList.push(product)
    }
    this.refreshSummary()
  }

  showEdit(id : string){
    /**
     * 
     */
  }

  remove(id : any){
    this.productList.forEach(element => {
      if(element.id == id){
        const index : number = this.productList.indexOf(element)
        this.productList.splice(index, 1)
        this.refreshSummary()
        return
      }
    })
  }

  refreshSummary(){
    this.totalQty = 0
    this.totalAmount = 0
    this.amountPaid = 0
    this.amountDue = 0
    this.discount = 0
    this.productList.forEach(element => {
      this.totalQty = this.totalQty + element.qty
      this.totalAmount = this.totalAmount + (element.qty * element.sellingPriceVatIncl)
    })
  }

  calculateAmounts(){
    if(!isNaN(this.amountPaid) && !isNaN(this.discount)){
      if(this.amountPaid >= 0 && this.discount >= 0){
        this.amountDue = this.totalAmount - this.amountPaid - this.discount
        return
      }
    }
    alert('Invalid entries')
  }

  setRecordMode(mode : string){
    this.recordMode = mode
  }

  async cancelSale(){
    this.productList = []
    this.clearProduct()
    const toast = await this.toastController.create({
      message: 'Sale canceled',
      duration: 2000,
      position: 'top'
    });
    await toast.present()
    this.router.navigate(['home'])
  }

  async confirmSale(){
    if(window.confirm('Confirm this sale?')){
      //confirm the sale
    }else{
      //do  not confirm
      return
    }
    /**
     * Sale confirmation logic here
     */
    var salesListNo = localStorage.getItem('active-list')!.toString()
    var sale : Sale = {
      no : this.no,
      salesListNo : salesListNo,
      customerName : this.customerName,
      customerMobile : this.customerMobile,
      customerLocation : this.customerLocation,
      totalAmount : this.totalAmount,
      totalPaid : this.amountPaid,
      totalDiscount : this.discount,
      totalDue : this.amountDue,
      totalCharges : 0,
      products : this.productList
    }

    if(salesListNo == null){
      alert('Could not perform operation, sales list not available')
      return
    }

    this.spinner.show()
    await this.http.post(API_URL+'/wms_sale/confirm', sale)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.router.navigate(['home'])
        alert('Sale confirmed')
        this.router.navigate(['home'])
        location.reload()
      }
    )
    .catch(
      async error => {
        console.log(error)
        const toast = await this.toastController.create({
          message: 'Sales Confirmation failed',
          duration: 2000,
          position: 'top'
        });
        await toast.present();
      }
    )

    
  }

 
}

interface LProductModel{
  id          : any
  barcode     : string
  code        : string
  description : string
  price       : number
  available   : number
}

interface LProduct {
  id          : any
  barcode     : string
  code        : string
  description : string
  sellingPriceVatIncl       : number
  qty         : number
  available   : number
}

interface Sale{
  no            : string
  salesListNo      : string
  customerName     : string
  customerMobile   : string
  customerLocation : string
  totalAmount      : number
  totalPaid       : number
  totalDiscount         : number
  totalCharges     : number
  totalDue        : number
  products         : LProduct[]
}



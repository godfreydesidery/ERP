import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { DataService } from 'src/app/services/data.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-sales-invoice',
  templateUrl: './sales-invoice.component.html',
  styleUrls: ['./sales-invoice.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class SalesInvoiceComponent implements OnInit {

  public invoiceNoLocked  : boolean = true
  public inputsLocked : boolean = true

  public priceLocked  : boolean = true

  public enableSearch : boolean = false
  public enableDelete : boolean = false
  public enableSave   : boolean = false

  closeResult    : string = ''

  logo!              : any
  address  : any 
  paymentDetails : any 

  blank          : boolean = false
  
  id             : any
  no             : string
  customer!      : ICustomer
  customerId     : any
  customerNo!    : string
  customerName!  : string
  salesAgent!     : ISalesAgent
  salesAgentId    : any
  salesAgentNo!   : string
  salesAgentName! : string
  status         : string
  balance  : number
  billingAddress   : string
  shippingAddress  : string
  totalVat         : number
  amountVatExcl    : number
  amountVatIncl    : number
  discount         : number
  otherCharges     : number
  netAmount        : number
  comments!      : string
  created        : string
  approved       : string
  invoiceDetails : ISalesInvoiceDetail[]
  invoices       : ISalesInvoice[]

  total            : number

  customerNames  : string[] = []
  salesAgentNames  : string[] = []

  //detail
  detailId            : any
  barcode             : string
  productId           : any
  code                : string
  description         : string
  qty                 : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
  costPriceVatIncl    : number
  costPriceVatExcl    : number

  descriptions : string[]

  companyName : string = ''


  //Debt Track

  amountToTransfer : number = 0
  amount : number = 0

  officerIncharge!      : ISalesAgent
  officerInchargeId     : any
  officerInchargeNo    : string = ''
  officerInchargeName  : string = ''

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id               = ''
    this.no               = ''
    this.status           = ''
    this.balance          = 0
    this.billingAddress      = ''
    this.shippingAddress     = ''
    this.totalVat            = 0
    this.amountVatExcl       = 0
    this.amountVatIncl       = 0
    this.discount            = 0
    this.otherCharges        = 0
    this.netAmount           = 0
    this.comments         = ''
    this.created          = ''
    this.approved         = ''
    this.invoiceDetails   = []
    this.invoices         = []

    this.total            = 0

    this.detailId            = ''
    this.barcode             = ''
    this.code                = ''    
    this.description         = ''
    this.qty                 = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0

    this.descriptions        = []
  }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
    this.paymentDetails = await this.data.getPaymentDetails()
    this.companyName = await this.data.getCompanyName()
    this.loadInvoices()
    this.loadCustomerNames()
    this.loadSalesAgentNames()
    this.loadProductDescriptions()
  }
  
  async save() {
    if(this.customerId == null || this.customerId == ''){
      alert('Customer information required')
      return
    }
    if(this.salesAgentId == null || this.salesAgentId == ''){
      alert('Sales Agent information required')
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var sales_invoices = {
      id           : this.id,
      customer     : {no : this.customerNo, name : this.customerName},
      salesAgent   : {no : this.salesAgentNo, alias : this.salesAgentName},
      billingAddress      : this.billingAddress,
      shippingAddress     : this.shippingAddress,
      totalVat            : this.totalVat,
      amountVatExcl       : this.amountVatExcl,
      amountVatIncl       : this.amountVatIncl,
      discount            : this.discount,
      otherCharges        : this.otherCharges,
      netAmount           : this.netAmount,
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){   
      this.spinner.show()
      await this.http.post<ISalesInvoice>(API_URL+'/sales_invoices/create', sales_invoices, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no         
          this.status       = data!.status
          this.balance      = data!.balance
          this.billingAddress      = data!.billingAddress
          this.shippingAddress     = data!.shippingAddress
          this.totalVat            = data!.totalVat
          this.amountVatExcl       = data!.amountVatExcl
          this.amountVatIncl       = data!.amountVatIncl
          this.discount            = data!.discount
          this.otherCharges        = data!.otherCharges
          this.netAmount           = data!.netAmount
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Invoice Created successifully')
          this.blank = true
          this.loadInvoices()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Invoice')
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<ISalesInvoice>(API_URL+'/sales_invoices/update', sales_invoices, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no
          this.status       = data!.status
          this.balance      = data!.balance
          this.billingAddress      = data!.billingAddress
          this.shippingAddress     = data!.shippingAddress
          this.totalVat            = data!.totalVat
          this.amountVatExcl       = data!.amountVatExcl
          this.amountVatIncl       = data!.amountVatIncl
          this.discount            = data!.discount
          this.otherCharges        = data!.otherCharges
          this.netAmount           = data!.netAmount
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Invoice Updated successifully')
          this.loadInvoices()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Invoice')
        }
      )
    }
  }

  async get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesInvoice>(API_URL+'/sales_invoices/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.lockAll()
        this.id             = data?.id
        this.no             = data!.no
        this.customerId     = data!.customer?.id
        this.customerNo     = data!.customer?.no
        this.customerName   = data!.customer?.name
        this.salesAgentId     = data!.salesAgent?.id
        this.salesAgentNo     = data!.salesAgent?.no   
        this.salesAgentName   = data!.salesAgent?.name
        this.status         = data!.status
        this.balance      = data!.balance
        this.billingAddress   = data!.customer.billingAddress
        this.shippingAddress  = data!.customer.shippingAddress
        this.totalVat         = data!.totalVat
        this.amountVatExcl    = data!.amountVatExcl
        this.amountVatIncl    = data!.amountVatIncl
        this.discount         = data!.discount
        this.otherCharges     = data!.otherCharges
        this.netAmount        = data!.netAmount
        this.comments       = data!.comments
        this.created        = data!.created
        this.approved       = data!.approved
        this.invoiceDetails = data!.salesInvoiceDetails
        this.refresh()
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Invoice')
      }
    )
  }

  async getByNo(no: string) {
    if(no == ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesInvoice>(API_URL+'/sales_invoices/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.lockAll()
        this.id           = data?.id
        this.no           = data!.no 
        this.customerId     = data!.customer?.id
        this.customerNo     = data!.customer?.no
        this.customerName   = data!.customer?.name
        this.salesAgentId     = data!.salesAgent?.id
        this.salesAgentNo     = data!.salesAgent?.no   
        this.salesAgentName   = data!.salesAgent?.name
        this.status       = data!.status
        this.balance      = data!.balance
        this.billingAddress   = data!.customer.billingAddress
        this.shippingAddress  = data!.customer.shippingAddress
        this.totalVat         = data!.totalVat
        this.amountVatExcl    = data!.amountVatExcl
        this.amountVatIncl    = data!.amountVatIncl
        this.discount         = data!.discount
        this.otherCharges     = data!.otherCharges
        this.netAmount        = data!.netAmount
        this.comments     = data!.comments
        this.created      = data!.created
        this.approved     = data!.approved
        this.invoiceDetails = data!.salesInvoiceDetails
        this.refresh()
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Invoice')
      }
    )
  }

  approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Invoice')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var invoice = {
      id : this.id,
      discount : this.discount,
      otherCharges : this.otherCharges  
    }
    this.spinner.show()
    this.http.put(API_URL+'/sales_invoices/approve', invoice, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadInvoices()
        this.get(id)
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not approve')
      }
    )
  }

  cancel(id: any) {
    if(!window.confirm('Confirm canceling of the selected Invoice')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var invoice = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/sales_invoices/cancel', invoice, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadInvoices()
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not cancel')
      }
    )
  }

  delete(id: any) {
    throw new Error('Method not implemented.');
  }
  
  async saveDetail() {
    if(this.customerId == null || this.customerId == ''){
      alert('Please enter customer information')
      return
    }
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Invoice
       */
      await this.save()
    }
    /**
     * Enter Invoice Detail
     */
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }   
    var detail = {
      salesInvoice : {id : this.id},
      product : {id : this.productId, code : this.code},
      qty : this.qty,
      costPriceVatIncl : this.costPriceVatIncl,
      costPriceVatExcl : this.costPriceVatExcl,
      sellingPriceVatIncl : this.sellingPriceVatIncl,
      sellingPriceVatExcl : this.sellingPriceVatExcl
    }
    this.spinner.show()
    await this.http.post(API_URL+'/sales_invoice_details/save', detail, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clearDetail()
        this.get(this.id)
        if(this.blank == true){
          this.blank = false
        }
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save detail')
      }
    )
  }

  getDetailByNo(no: string) {
    throw new Error('Method not implemented.');
  }

  deleteDetail(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/sales_invoice_details/delete?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.get(this.id)
      }
    )
    .catch(
      error => {ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not remove detail')
      }
    )
  }

  loadInvoices(){
    this.invoices = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<ISalesInvoice[]>(API_URL+'/sales_invoices', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.invoices.push(element)
        })
      }
    )
  }

  async archive(id: any) {
    if(id == null || id == ''){
      window.alert('Please select Invoice to archive')
      return
    }
    if(!window.confirm('Confirm archiving of the selected Invoice')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var invoice = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/sales_invoices/archive', invoice, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadInvoices()
        alert('Invoice archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  async archiveAll() {
    if(!window.confirm('Confirm archiving Invoices. All PAID Invoices will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/sales_invoices/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadInvoices()
        alert('Invoices archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  unlockAll(){
    this.invoiceNoLocked  = false
    this.inputsLocked = false   
  }

  lockAll(){
    this.invoiceNoLocked  = true
    this.inputsLocked = true
  }

  clear(){
    this.id             = ''
    this.no             = ''
    this.status         = ''
    this.balance        = 0
    this.billingAddress      = ''
    this.shippingAddress     = ''
    this.totalVat            = 0
    this.amountVatExcl       = 0
    this.amountVatIncl       = 0
    this.discount            = 0
    this.otherCharges        = 0
    this.netAmount           = 0
    this.comments       = ''
    this.created        = ''
    this.approved       = ''
    this.invoiceDetails = []
    this.customerNo     = ''
    this.customerName   = ''
    this.salesAgentNo           = ''
    this.salesAgentName         = ''
  }

  clearDetail(){
    this.detailId         = ''
    this.barcode          = ''
    this.code             = ''
    this.description      = ''
    this.qty              = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0
  }

  refresh(){
    this.priceLocked = true
    this.total = 0
    this.amountVatExcl = 0
    this.amountVatIncl = 0
    this.totalVat = 0
    this.netAmount = 0
    this.invoiceDetails.forEach(element => {
      this.total = this.total + element.sellingPriceVatIncl*element.qty
      this.amountVatExcl = this.amountVatExcl + element.sellingPriceVatExcl*element.qty
      this.amountVatIncl = this.amountVatIncl + element.sellingPriceVatIncl*element.qty
    })
    this.totalVat = this.amountVatIncl - this.amountVatExcl
    this.showNet()
  }

  showNet(){
    this.netAmount = this.amountVatIncl - (this.otherCharges * -1) - this.discount
  }

  unlockPrice(){
    this.priceLocked = false
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  searchProduct(barcode : string, code : string, description : string){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    if(barcode != ''){
      //search by barcode
      this.spinner.show()
      this.http.get<IProduct>(API_URL+'/products/get_by_barcode?barcode='+barcode, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.productId = data!.id
          this.barcode = data!.barcode
          this.code = data!.code
          this.description = data!.description
          this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
        }
      )
      .catch(error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Product not found')
      })
    }else if(code != ''){
      this.spinner.show()
      this.http.get<IProduct>(API_URL+'/products/get_by_code?code='+code, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.productId = data!.id
          this.barcode = data!.barcode
          this.code = data!.code
          this.description = data!.description
          this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Product not found')
      })
    }else{
      //search by description
      this.spinner.show()
      this.http.get<IProduct>(API_URL+'/products/get_by_description?description='+description, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.productId = data!.id
          this.barcode = data!.barcode
          this.code = data!.code
          this.description = data!.description
          this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
        }
      )
      .catch(error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Product not found')
      })
    }
  }

  searchDetail(productId : any, detailId :any){    
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IProduct>(API_URL+'/products/get?id='+productId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.productId = data!.id
        this.barcode = data!.barcode
        this.code = data!.code
        this.description = data!.description
      }
    )
    .catch(error => {
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load product')
    })
    this.spinner.show()
    this.http.get<ISalesInvoiceDetail>(API_URL+'/sales_invoice_details/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.detailId = data!.id
        this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
        this.qty = data!.qty
      }
    )
    .catch(error => {
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  async requestNo(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/sales_invoices/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        this.invoiceNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Invoice No request failed')
      }
    )
  }

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title', size : 'xl'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  open(content : any, productId : string, detailId : string) {
    if(this.customerNo == '' || this.customerNo == null){
      alert('Please enter customer information')
      return
    }  
    if(productId != ''){
      this.searchDetail(productId, detailId)
    }
    
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    this.clearDetail()
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  async loadCustomerNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/customers/get_names', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.customerNames = []
        data?.forEach(element => {
          this.customerNames.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not load customer names')
      }
    )
  }

  async loadProductDescriptions(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/products/get_descriptions', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.descriptions = []
        data?.forEach(element => {
          this.descriptions.push(element)
        })
        console.log(data)
      },
      error => {
        console.log(error)
        alert('Could not load product descriptions')
      }
    )
  }

  async searchCustomer(name: string) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ICustomer>(API_URL+'/customers/get_by_name?name='+name, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.customerId = data?.id
        this.customerNo = data!.no
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('Customer not found')
        this.customerId = ''
        this.customerNo = ''
        this.customerName = ''
      }
    )
  }

  async searchSalesAgent(name: string) {
    if(name == ''){
      this.salesAgentId   = ''
      this.salesAgentNo   = ''
      this.salesAgentName = ''
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesAgent>(API_URL+'/sales_agents/get_by_name?name='+name, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.salesAgentId = data?.id
        this.salesAgentNo = data!.no
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('SalesAgent not found')
        this.salesAgentId   = ''
        this.salesAgentNo   = ''
        this.salesAgentName = ''
      }
    )
  }

  async loadSalesAgentNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/sales_agents/get_names', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.salesAgentNames = []
        data?.forEach(element => {
          this.salesAgentNames.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not load sales agent names')
      }
    )
  }

  async searchOfficerIncharge(name: string) {
    if (name == '') {
      this.officerInchargeId = ''
      this.officerInchargeNo = ''
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    this.spinner.show()
    await this.http.get<ISalesAgent>(API_URL+'/sales_agents/get_by_name?name='+name, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.officerInchargeId = data?.id
        this.officerInchargeNo = data!.no
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('Name not found')
        this.officerInchargeId = ''
        this.officerInchargeNo = ''
        this.officerInchargeName = ''
      }
    )
  }


  async post() {
    if(this.customerName == ''){
      alert('Please select customer')
      return
    }
    if(this.officerInchargeName == ''){
      alert('Please select officer incharge')
      return
    }
    if(!window.confirm('Transfer the specified amount to debt tracker?')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var trc = {
      debt : {id : null},
      salesInvoice          : {id : this.id},
      customer : {id : this.customerId},
      officerIncharge : {id : this.officerInchargeId}, 
      amount : this.amountToTransfer
        
    }
    this.spinner.show()
    await this.http.post(API_URL+'/debt_trackers/create_from_sales_invoice', trc, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        //this.searchSalesAgent(this.salesAgentName)
        alert('Transfer successiful')
        this.get(this.id)
        this.amountToTransfer    = 0
        this.officerInchargeId   = ''
        this.officerInchargeNo   = ''
        this.officerInchargeName = ''
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could process')
      }
    )
  }

  exportToPdf = () => {
    if(this.id == '' || this.id == null){
      return
    }
    var header = ''
    var footer = ''
    var title  = 'Invoice'
    var logo : any = ''
    var total : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 80, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 80, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'Code', fontSize : 9, bold : true}, 
        {text : 'Description', fontSize : 9, bold : true},
        {text : 'Qty', fontSize : 9, bold : true},
        {text : 'Price(VAT Incl)', fontSize : 9, bold : true},
        {text : 'Total', fontSize : 9}
      ]
    ]    
    this.invoiceDetails.forEach((element) => {
      total = total + element.qty*element.sellingPriceVatIncl
      var detail = [
        {text : element.product.code.toString(), fontSize : 9}, 
        {text : element.product.description.toString(), fontSize : 9},
        {text : element.qty.toString(), fontSize : 9},  
        {text : element.sellingPriceVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'},
        {text : (element.qty*element.sellingPriceVatIncl).toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'},        
      ]
      report.push(detail)
    })
    var detailSummary = [
      {text : '', fontSize : 9}, 
      {text : '', fontSize : 9},
      {text : '', fontSize : 9},  
      {text : '', fontSize : 9},
      {text : total.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'},        
    ]
    //report.push(detailSummary)
    const docDefinition = {
      header: '',
      watermark : { text : this.companyName, color: 'blue', opacity: 0.1, bold: true, italics: false },
        content : [
          
          {
            columns : 
            [
              logo,
              {
                width : 10,
                columns : [
                  {text : ''}
                ]
              },
              {
                width : 240,
                columns : [
                  this.address
                ]
              },
              {
                width : 200,
                layout : 'noBorders',
                table : {
                  widths : [220],
                  body : [
                    [
                      ' '
                    ],
                    [
                      {text : title, fontSize : 12, bold : true}
                    ],
                    [
                      {text : '# '+ this.no, fontSize : 11, bold : true}
                    ],
                    [
                      ''
                    ],
                    [
                      {text : 'Customer: '+this.customerName, fontSize : 8}
                    ],
                    [
                      {text : 'Status: '+this.status, fontSize : 8}
                    ],
                  ]
                }
              },
              
              
            ],
          },
          '  ',
          
          {
            layout : 'noBorders',
            table : {
              widths : [160, 160],
              body : [
                [
                  {text : 'Bill To', fontSize : 9}, 
                  {text : 'Ship To', fontSize : 9} 
                ],
                [
                  {text : this.billingAddress, fontSize : 9}, 
                  {text : this.shippingAddress, fontSize : 9} 
                ]
              ]
            },
          },
          ' ',
          {
            //layout : 'headerLineOnly',
            table : {
                headerRows : 1,
                widths : ['auto', 230, 'auto', 70, 80],
                body : report
            }
        },
        {
          layout : 'noBorders',
          table : {
            widths : [295, 75, 80],
            body : [
              [
                {},
                {text : 'Total VAT', fontSize : 8}, 
                {text : this.totalVat.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right'} 
              ],
              [
                {},
                {text : 'Amount VAT Excl', fontSize : 8}, 
                {text : this.amountVatExcl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right'} 
              ],
              [

                {},
                {text : 'Amount VAT Incl', fontSize : 8}, 
                {text : this.amountVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right'} 
              ],
              [
                {},
                {text : 'Discount', fontSize : 8}, 
                {text : this.discount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right'} 
              ],
              [
                {},
                {text : 'Other Charges', fontSize : 8}, 
                {text : this.otherCharges.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right'} 
              ],
              [
                {},
                {text : 'Net Amount', fontSize : 12}, 
                {text : this.netAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 12, alignment : 'right', bold : true} 
              ]
            ]
          },
        },
        ' ',
        'Payment Details',
        {
          layout : 'noBorders',
          table : {
            widths : [150, 150, 150],
            body : [
              this.paymentDetails
            ]
          },
        },
        ' ',
        ' ',   
        ' ',
        'Verified ____________________________________', 
        ' ',
        ' ',
        'Approved __________________________________',             
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }
}

interface ISalesInvoice{
  id           : any
  no           : string
  customer     : ICustomer
  salesAgent   : ISalesAgent
  status       : string
  balance :  number
  billingAddress : string
  shippingAddress : string
  totalVat : number
  amountVatExcl : number
  amountVatIncl : number
  discount : number
  otherCharges : number
  netAmount : number
  comments     : string
  invoiceDate  : Date
  validUntil   : Date
  created      : string
  approved     : string
  salesInvoiceDetails   : ISalesInvoiceDetail[]
}

interface ISalesInvoiceDetail{
  id               : any
  qty              : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
  costPriceVatIncl    : number
  costPriceVatExcl    : number
  product          : IProduct
}

interface IProduct{
  id               : any
  barcode          : string
  code             : string
  description      : string
  packSize         : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
  costPriceVatIncl : number
  costPriceVatExcl : number
}

interface ICustomer{
  id                  : any
  no                  : string
  name                : string
  contactName         : string
  active              : boolean
  tin                 : string
  vrn                 : string
  creditLimit         : number
  invoiceLimit        : number
  creditDays          : number
  physicalAddress     : string
  postCode            : string
  postAddress         : string
  telephone           : string
  mobile              : string
  email               : string
  fax                 : string
  bankAccountName     : string
  bankPhysicalAddress : string
  bankPostAddress     : string
  bankPostCode        : string
  bankName            : string
  bankAccountNo       : string

  billingAddress      : string
  shippingAddress     : string
}

interface ICustomerName{
  names : string[]
}

interface ISalesAgent{
  id                  : any
  no                  : string
  name                : string
}

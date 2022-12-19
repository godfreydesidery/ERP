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
  selector: 'app-quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class QuotationComponent implements OnInit {

  public quotationNoLocked  : boolean = true
  public inputsLocked : boolean = true

  public enableSearch : boolean = false
  public enableDelete : boolean = false
  public enableSave   : boolean = false

  closeResult      : string = ''

  blank            : boolean = false

  logo!              : any
  address  : any 
  
  id               : any
  no               : string
  customer!        : ICustomer
  customerId       : any
  customerNo!      : string
  customerName!    : string
  status           : string
  billingAddress   : string
  shippingAddress  : string
  totalVat         : number
  amountVatExcl    : number
  amountVatIncl    : number
  discount         : number
  otherCharges     : number
  netAmount        : number
  comments!        : string
  created          : string
  approved         : string
  quotationDetails : IQuotationDetail[]
  quotations       : IQuotation[]



  total            : number

  customerNames    : string[] = []

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

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id                  = ''
    this.no                  = ''
    this.status              = ''
    this.billingAddress      = ''
    this.shippingAddress     = ''
    this.totalVat            = 0
    this.amountVatExcl       = 0
    this.amountVatIncl       = 0
    this.discount            = 0
    this.otherCharges        = 0
    this.netAmount           = 0
    this.comments            = ''
    this.created             = ''
    this.approved            = ''
    this.quotationDetails    = []
    this.quotations          = []

    this.total               = 0

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
    this.loadQuotations()
    this.loadCustomerNames()
    this.loadProductDescriptions()
  }
  
  async save() {
    if(this.customerId == null || this.customerId == ''){
      alert('Customer information missing')
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var quotations = {
      id           : this.id,
      customer     : {no : this.customerNo, name : this.customerName},
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
      await this.http.post<IQuotation>(API_URL+'/quotations/create', quotations, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no         
          this.status       = data!.status
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
          alert('Quotation Created successifully')
          this.blank = true
          this.loadQuotations()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Quotation')
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<IQuotation>(API_URL+'/quotations/update', quotations, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no
          this.status       = data!.status
          this.comments     = data!.comments
          this.billingAddress      = data!.billingAddress
          this.shippingAddress     = data!.shippingAddress
          this.totalVat            = data!.totalVat
          this.amountVatExcl       = data!.amountVatExcl
          this.amountVatIncl       = data!.amountVatIncl
          this.discount            = data!.discount
          this.otherCharges        = data!.otherCharges
          this.netAmount           = data!.netAmount
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Quotation Updated successifully')
          this.loadQuotations()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Quotation')
        }
      )
    }
  }

  async get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IQuotation>(API_URL+'/quotations/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.lockAll()
        this.id               = data?.id
        this.no               = data!.no
        this.customerId       = data!.customer.id
        this.customerNo       = data!.customer.no
        this.customerName     = data!.customer.name
        this.status           = data!.status
        this.billingAddress   = data!.customer.billingAddress
        this.shippingAddress  = data!.customer.shippingAddress
        this.totalVat         = data!.totalVat
        this.amountVatExcl    = data!.amountVatExcl
        this.amountVatIncl    = data!.amountVatIncl
        this.discount         = data!.discount
        this.otherCharges     = data!.otherCharges
        this.netAmount        = data!.netAmount
        this.comments         = data!.comments
        this.created          = data!.created
        this.approved         = data!.approved
        this.quotationDetails = data!.quotationDetails
        this.refresh()

      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Quotation')
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
    await this.http.get<IQuotation>(API_URL+'/quotations/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.lockAll()
        this.id           = data?.id
        this.no           = data!.no 
        this.customerId   = data!.customer.id
        this.customerNo   = data!.customer.no
        this.customerName = data!.customer.name  
        this.status       = data!.status
        this.billingAddress      = data!.customer.billingAddress
        this.shippingAddress     = data!.customer.shippingAddress
        this.totalVat            = data!.totalVat
        this.amountVatExcl       = data!.amountVatExcl
        this.amountVatIncl       = data!.amountVatIncl
        this.discount            = data!.discount
        this.otherCharges        = data!.otherCharges
        this.netAmount           = data!.netAmount
        this.comments     = data!.comments
        this.created      = data!.created
        this.approved     = data!.approved
        this.quotationDetails = data!.quotationDetails
        this.refresh()
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Quotation')
      }
    )
  }

  approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Quotation')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var quotation = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/quotations/approve', quotation, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadQuotations()
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
    if(!window.confirm('Confirm canceling of the selected Quotation')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var quotation = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/quotations/cancel', quotation, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadQuotations()
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
       * First Create a new Quotation
       */
      await this.save()
    }
    /**
     * Enter Quotation Detail
     */
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }   
    var detail = {
      quotation : {id : this.id},
      product : {id : this.productId, code : this.code},
      qty : this.qty,
      sellingPriceVatIncl : this.sellingPriceVatIncl,
      sellingPriceVatExcl : this.sellingPriceVatExcl
    }
    this.spinner.show()
    await this.http.post(API_URL+'/quotation_details/save', detail, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clearDetail()
        this.get(this.id)
        if(this.blank == true){
          this.blank = false
          //this.loadQuotations()
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
    this.http.delete(API_URL+'/quotation_details/delete?id='+id, options)
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

  loadQuotations(){
    this.quotations = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IQuotation[]>(API_URL+'/quotations', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.quotations.push(element)
        })
      }
    )
  }

  async archive(id: any) {
    if(id == null || id == ''){
      window.alert('Please select Quotation to archive')
      return
    }
    if(!window.confirm('Confirm archiving of the selected Quotation')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var quotation = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/quotations/archive', quotation, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadQuotations()
        alert('Quotation archived successifully')
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
    if(!window.confirm('Confirm archiving Quotations. All PAID Quotations will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/quotations/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadQuotations()
        alert('Quotations archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  refresh(){
    this.total = 0
    this.amountVatExcl = 0
    this.amountVatIncl = 0
    this.totalVat = 0
    this.netAmount = 0
    this.quotationDetails.forEach(element => {
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

  unlockAll(){
    this.quotationNoLocked  = false
    this.inputsLocked = false   
  }

  lockAll(){
    this.quotationNoLocked  = true
    this.inputsLocked = true
  }

  clear(){
    this.id               = ''
    this.no               = ''
    this.status           = ''
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
    this.quotationDetails = []
    this.customerNo       = ''
    this.customerName     = ''
  }

  clearDetail(){
    this.detailId            = ''
    this.barcode             = ''
    this.code                = ''
    this.description         = ''
    this.qty                 = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0
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
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
          this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
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
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
          this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
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
          this.sellingPriceVatIncl = data!.sellingPriceVatIncl
          this.sellingPriceVatExcl = data!.sellingPriceVatExcl
          this.costPriceVatIncl = data!.costPriceVatIncl
          this.costPriceVatExcl = data!.costPriceVatExcl
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
    this.http.get<IQuotationDetail>(API_URL+'/quotation_details/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.detailId = data!.id
        this.sellingPriceVatIncl = data!.sellingPriceVatIncl
        this.sellingPriceVatExcl = data!.sellingPriceVatExcl
        this.costPriceVatIncl = data!.costPriceVatIncl
        this.costPriceVatExcl = data!.costPriceVatExcl
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
    await this.http.get<any>(API_URL+'/quotations/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        this.quotationNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Quotation No request failed')
      }
    )
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

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
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
        this.billingAddress = data!.billingAddress
        this.shippingAddress = data!.shippingAddress
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('Customer not found')
        this.customerId = ''
        this.customerNo = ''
        this.customerName = ''
        this.billingAddress = ''
        this.shippingAddress = ''
      }
    )
  }

 

  exportToPdf = () => {
    if(this.id == '' || this.id == null){
      return
    }
    this.refresh()
    var header = ''
    var footer = ''
    var title  = 'Quotation'
    var logo : any = ''
    var total : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9},
        {text : 'Qty', fontSize : 9},
        {text : 'Price', fontSize : 9},
        {text : 'Total', fontSize : 9}
      ]
    ]    
    this.quotationDetails.forEach((element) => {
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
    report.push(detailSummary)
    const docDefinition = {
      header: '',
      watermark : { text : title, color: 'blue', opacity: 0.1, bold: true, italics: false },
        content : [
          {
            columns : 
            [
              logo,
              {width : 10, columns : [[]]},
              {
                width : 300,
                columns : [
                  this.address
                ]
              },
            ]
          },
          '  ',
          '  ',
          {text : title, fontSize : 12, bold : true},
          '  ',
          {
            layout : 'noBorders',
            table : {
              widths : [75, 300],
              body : [
                [
                  {text : 'Quotation No', fontSize : 9}, 
                  {text : this.no, fontSize : 9} 
                ],
                [
                  {text : 'Customer', fontSize : 9}, 
                  {text : this.customerName, fontSize : 9} 
                ],
                [
                  {text : 'Status', fontSize : 9}, 
                  {text : this.status, fontSize : 9} 
                ]
              ]
            },
          },
          ' ',
          {
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
            table : {
                headerRows : 1,
                widths : ['auto', 230, 'auto', 70, 80],
                body : report
            }
        },
        ' ',
        ' ',
        {
          table : {
            widths : [75, 75],
            body : [
              [
                {text : 'Total VAT', fontSize : 9}, 
                {text : this.totalVat.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Amount VAT Excl', fontSize : 9}, 
                {text : this.amountVatExcl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Amount VAT Incl', fontSize : 9}, 
                {text : this.amountVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Discount', fontSize : 9}, 
                {text : this.discount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Other Charges', fontSize : 9}, 
                {text : this.otherCharges.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Net Amount', fontSize : 9}, 
                {text : this.netAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ]
            ]
          },
        },
        '  ',
        ' ',
        ' ',   
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

interface IQuotation{
  id            : any
  no            : string
  customer      : ICustomer
  status        : string
  comments      : string
  quotationDate : Date
  validUntil    : Date

  billingAddress : string
  shippingAddress : string
  totalVat : number
  amountVatExcl : number
  amountVatIncl : number
  discount : number
  otherCharges : number
  netAmount : number

  created       : string
  approved      : string
  quotationDetails : IQuotationDetail[]
}

interface IQuotationDetail{
  id                  : any
  qty                 : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
  costPriceVatIncl    : number
  costPriceVatExcl    : number
  product             : IProduct
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
  quotationLimit      : number
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

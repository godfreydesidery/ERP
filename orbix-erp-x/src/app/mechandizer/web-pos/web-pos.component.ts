import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
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
  selector: 'app-web-pos',
  templateUrl: './web-pos.component.html',
  styleUrls: ['./web-pos.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class WebPosComponent implements OnInit {



  public priceLocked  : boolean = true

  closeResult    : string = ''



  blank          : boolean = false
  
  id             : any
  
  salesAgent!     : ISalesAgent
  salesAgentId    : any
  salesAgentNo!   : string
  salesAgentName! : string
  
  created        : string
  approved       : string
  webPoss : IWebPos[]

  total            : number
  amount           : number

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

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id               = ''
    
    this.created          = ''
    this.approved         = ''
    this.webPoss   = []

    this.total            = 0
    this.amount           = 0

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
    this.companyName = await this.data.getCompanyName()
    this.loadSalesAgentNames()
    this.loadProductDescriptions()
    this.get()
  }
  

  approve(amount : number) {
    if(this.salesAgentId == null){
      alert('Please select Sales Agent')
      return
    }
    if(amount != this.total){
      alert('Amount entered does not match total sale')
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    var approveData : IApproveData = {
      amount : this.total,
      salesAgent : {id : this.salesAgentId, no : this.salesAgentNo, name : this.salesAgentName}
    }
    
    this.spinner.show()
    this.http.put(API_URL+'/web_pos/approve', approveData, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.clearDetail()
        this.get()
        alert('Sale approved successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not approve')  
        this.clear()
        this.clearDetail()
        this.get()  
      }
    )
  }

  async saveDetail() {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }   
    var detail = {
      id : this.detailId,
      product : {id : this.productId, code : this.code},
      qty : this.qty,
      costPriceVatIncl : this.costPriceVatIncl,
      costPriceVatExcl : this.costPriceVatExcl,
      sellingPriceVatIncl : this.sellingPriceVatIncl,
      sellingPriceVatExcl : this.sellingPriceVatExcl
    }
    this.spinner.show()
    await this.http.post(API_URL+'/web_pos/save', detail, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clearDetail()
        this.get()
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save detail')
      }
    )
  }

  async get() {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IWebPos[]>(API_URL+'/web_poses', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.webPoss = data!
        this.refresh()
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Details')
      }
    )
  }

  

  deleteDetail(id: any) {
    if(!window.confirm('Remove detail?')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/web_pos/delete?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.get()
      }
    )
    .catch(
      error => {ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not remove detail')
      }
    )
  }

  

  clear(){
    this.id             = ''
    this.created        = ''
    this.approved       = ''
    this.webPoss = []
    this.salesAgentId   = ''
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
    this.total = 0
    this.amount = 0
    this.webPoss.forEach(element => {
      this.total = this.total + element.sellingPriceVatIncl*element.qty
      
    })
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

  async searchDetail(productId : any, detailId :any){    
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IProduct>(API_URL+'/products/get?id='+productId, options)
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
    await this.http.get<IWebPos>(API_URL+'/web_pos/get?id='+detailId, options)
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

  

  showList(listContent: any) {
    if(this.salesAgentId == null || this.salesAgentId == '' ){
      alert('Please select Sales Agent')
      return
    }
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  open(content : any, productId : string, detailId : string) {
    
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

  unlockPrice(){
    this.priceLocked = false
  }

  
}



interface IWebPos{
  id               : any
  qty              : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
  costPriceVatIncl    : number
  costPriceVatExcl    : number
  product          : IProduct
  created          : string
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

interface IApproveData{
  amount : number
  salesAgent : ISalesAgent
}
import { Component, OnInit } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';
import {  DatePipe} from '@angular/common'

const pdfMakeX = require('pdfmake/build/pdfmake.js');
const pdfFontsX = require('pdfmake-unicode/dist/pdfmake-unicode.js');
pdfMakeX.vfs = pdfFontsX.pdfMake.vfs;
import * as pdfMake from 'pdfmake/build/pdfmake';
import { DataService } from 'src/app/services/data.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { ConnectableObservable, finalize } from 'rxjs';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-sales-list',
  templateUrl: './sales-list.component.html',
  styleUrls: ['./sales-list.component.scss']
})
export class SalesListComponent implements OnInit {
  public slstNoLocked  : boolean = true
  public inputsLocked : boolean = true
  public valuesLocked : boolean = true

  public enableSearch : boolean = false
  public enableDelete : boolean = false
  public enableSave   : boolean = false

  logo!              : any 
  closeResult        : string = ''
  disablePriceChange : any = false

  blank          : boolean = false
  
  id             : any
  no             : string
  issueDate!      : Date
  customer!      : ICustomer
  customerId     : any
  customerNo!    : string
  customerName!  : string
  salesAgent!     : ISalesAgent
  salesAgentId    : any
  salesAgentNo!   : string
  salesAgentName! : string
  status         : string
  comments!      : string
  created        : string
  approved       : string
  salesListDetails : ISalesListDetail[]
  salesLists       : ISalesList[]


  total            : number

  totalAmountPacked    : number
  totalSales           : number
  totalOffered         : number
  totalReturns         : number
  totalDamages         : number
  totalDeficit         : number

  totalDiscounts       : number
  totalExpenditures     : number
  totalBank            : number
  totalCash            : number

  customerNames  : string[] = []
  salesAgentNames  : string[] = []

  //detail
  detailId            : any
  barcode             : string
  productId           : any
  code                : string
  description         : string
  
  totalPacked         : number
  qtySold             : number
  qtyOffered          : number
  qtyReturned         : number
  qtyDamaged          : number   
  costPriceVatIncl    : number
  costPriceVatExcl    : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number

  finalQty            : number = 0

  descriptions : string[]

  address : any
  companyName : string = ''

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService,
              public datepipe : DatePipe) {
    this.id               = ''
    this.no               = ''
    this.status           = ''
    this.comments         = ''
    this.created          = ''
    this.approved         = ''
    this.salesListDetails   = []
    this.salesLists         = []

    this.total            = 0

    this.totalAmountPacked    = 0
    this.totalSales           = 0
    this.totalOffered         = 0
    this.totalReturns         = 0
    this.totalDamages         = 0
    this.totalDeficit         = 0

    this.totalDiscounts       = 0
    this.totalExpenditures     = 0
    this.totalBank            = 0
    this.totalCash            = 0

    this.detailId            = ''
    this.barcode             = ''
    this.code                = ''    
    this.description         = ''
    this.totalPacked         = 0
    this.qtySold             = 0
    this.qtyOffered          = 0
    this.qtyReturned         = 0
    this.qtyDamaged          = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0

    this.descriptions        = []
  }

  async ngOnInit(): Promise<void> {
    this.address = await this.data.getAddress()
    this.logo = await this.data.getLogo()
    this.companyName = await this.data.getCompanyName()
    this.loadSalesLists()
    this.loadCustomerNames()
    this.loadSalesAgentNames()
    this.loadProductDescriptions()
  }

  async save() {   
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var salesList = {
      id           : this.id,
      customer     : {no : this.customerNo, name : this.customerName},
      salesAgent     : {no : this.salesAgentNo, alias : this.salesAgentName},
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){  
      this.spinner.show() 
      await this.http.post<ISalesList>(API_URL+'/sales_lists/create', salesList, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id                   = data?.id
          this.no                   = data!.no         
          this.status               = data!.status
          this.comments             = data!.comments
          this.created              = data!.created
          this.approved             = data!.approved
          this.totalAmountPacked    = data!.totalAmountPacked
          this.totalSales           = data!.totalSales
          this.totalOffered         = data!.totalOffered
          this.totalReturns         = data!.totalReturns
          this.totalDamages         = data!.totalDamages
          this.totalDeficit         = data!.totalDeficit
          this.totalDiscounts       = data!.totalDiscounts
          this.totalExpenditures     = data!.totalExpenditures
          this.totalBank            = data!.totalBank
          this.totalCash            = data!.totalCash
          this.get(this.id)
          alert('Sales List created successifully')
          this.blank = true
          this.loadSalesLists()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Sales List')
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<ISalesList>(API_URL+'/sales_lists/update', salesList, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id                   = data?.id
          this.no                   = data!.no
          this.status               = data!.status
          this.comments             = data!.comments
          this.created              = data!.created
          this.approved             = data!.approved
          this.totalAmountPacked    = data!.totalAmountPacked
          this.totalSales           = data!.totalSales
          this.totalOffered         = data!.totalOffered
          this.totalReturns         = data!.totalReturns
          this.totalDamages         = data!.totalDamages
          this.totalDeficit         = data!.totalDeficit
          this.totalDiscounts       = data!.totalDiscounts
          this.totalExpenditures     = data!.totalExpenditures
          this.totalBank            = data!.totalBank
          this.totalCash            = data!.totalCash
          this.get(this.id)
          alert('Sales List updated successifully')
          this.loadSalesLists()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Sales List')
        }
      )
    }
  }

  refresh(){
    if(this.status == 'PENDING'){
      this.valuesLocked = false
    }else{
      this.valuesLocked = true
    }
  }

  async get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesList>(API_URL+'/sales_lists/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.id              = data?.id
        this.no              = data!.no
        this.issueDate       = data!.issueDate
        this.customerId      = data!.customer.id
        this.customerNo      = data!.customer.no
        this.customerName    = data!.customer.name
        this.salesAgentId     = data!.salesAgent.id
        this.salesAgentNo     = data!.salesAgent.no
        this.salesAgentName   = data!.salesAgent.name
        this.status               = data!.status
        this.comments             = data!.comments
        this.created              = data!.created
        this.approved             = data!.approved
        this.totalAmountPacked    = data!.totalAmountPacked
        this.totalSales           = data!.totalSales
        this.totalOffered         = data!.totalOffered
        this.totalReturns         = data!.totalReturns
        this.totalDamages         = data!.totalDamages
        this.totalDeficit         = data!.totalDeficit
        this.totalDiscounts       = data!.totalDiscounts
        this.totalExpenditures     = data!.totalExpenditures
        this.totalBank            = data!.totalBank
        this.totalCash            = data!.totalCash
        this.refresh()
        this.salesListDetails   = data!.salesListDetails
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Sales List')
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
    await this.http.get<ISalesList>(API_URL+'/sales_lists/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id            = data?.id
        this.no            = data!.no 
        this.issueDate       = data!.issueDate
        this.customerId    = data!.customer.id
        this.customerNo    = data!.customer.no
        this.customerName  = data!.customer.name  
        this.salesAgentId     = data!.salesAgent.id
        this.salesAgentNo     = data!.salesAgent.no
        this.salesAgentName   = data!.salesAgent.name
        this.status               = data!.status
        this.comments             = data!.comments
        this.created              = data!.created
        this.approved             = data!.approved
        this.totalAmountPacked    = data!.totalAmountPacked
        this.totalSales           = data!.totalSales
        this.totalOffered         = data!.totalOffered
        this.totalReturns         = data!.totalReturns
        this.totalDamages         = data!.totalDamages
        this.totalDeficit         = data!.totalDeficit
        this.totalDiscounts       = data!.totalDiscounts
        this.totalExpenditures    = data!.totalExpenditures
        this.totalBank            = data!.totalBank
        this.totalCash            = data!.totalCash
        this.refresh()
        this.salesListDetails   = data!.salesListDetails
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Sales List')
      }
    )
  }

  

  async approve(id: any) {
    if(!window.confirm('Confirm approval and post of the selected Sales List')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var slst = {
      id                   : this.id,     
      totalAmountPacked    : this.totalAmountPacked,   
      totalSales           : this.totalSales,           
      totalOffered         : this.totalOffered,         
      totalReturns         : this.totalReturns,         
      totalDamages         : this.totalDamages,         
      totalDeficit         : this.totalDeficit,         
      totalDiscounts       : this.totalDiscounts,       
      totalExpenditures    : this.totalExpenditures,     
      totalBank            : this.totalBank,            
      totalCash            : this.totalCash
    }
    this.spinner.show()
    await this.http.put(API_URL+'/sales_lists/approve', slst, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadSalesLists()
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

  async cancel(id: any) {
    if(!window.confirm('Confirm canceling of the selected Sales List')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var slst = {
      id : this.id   
    }
    this.spinner.show()
    await this.http.put(API_URL+'/sales_lists/cancel', slst, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadSalesLists()
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
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Sales List
       */
      alert('Sales List not available, the system will create a new Sales List')
      this.save()
    }else{
      /**
       * Enter Sales List Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        salesList           : {id : this.id},
        product             : {id : this.productId, code : this.code},
        totalPacked         : this.totalPacked,
        qtySold             : this.qtySold,
        qtyOffered          : this.qtyOffered,
        qtyReturned         : this.qtyReturned,
        qtyDamaged          : this.qtyDamaged,  
        costPriceVatIncl    : this.costPriceVatIncl,
        costPriceVatExcl    : this.costPriceVatExcl,
        sellingPriceVatIncl : this.sellingPriceVatIncl,
        sellingPriceVatExcl : this.sellingPriceVatExcl
      }
      this.spinner.show()
      await this.http.post(API_URL+'/sales_list_details/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearDetail()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadSalesLists()
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
  }

  

  getDetailByNo(no: string) {
    throw new Error('Method not implemented.');
  }

  loadSalesLists(){
    this.salesLists = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.http.get<ISalesList[]>(API_URL+'/sales_lists', options)
    .toPromise()
    .then(
      data => {
        console.log(data)
        data?.forEach(element => {
          this.salesLists.push(element)
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
    var slst = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/sales_lists/archive', slst, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadSalesLists()
        alert('Sales List archived successifully')
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
    if(!window.confirm('Confirm archiving Sales Lists. All Posted  and debt free documents will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/sales_lists/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadSalesLists()
        alert('All Posted and Debt free archived successifully')
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
    this.slstNoLocked  = false
    this.inputsLocked = false
    this.valuesLocked = true   
  }

  lockAll(){
    this.slstNoLocked  = true
    this.inputsLocked = true
  }

  clear(){
    this.id                   = ''
    this.no                   = ''
    this.status               = ''
    this.comments             = ''
    this.created              = ''
    this.approved             = ''
    this.salesListDetails   = []
    this.customerNo           = ''
    this.customerName         = ''
    this.salesAgentNo           = ''
    this.salesAgentName         = ''
    this.totalAmountPacked    = 0
    this.totalSales           = 0
    this.totalOffered         = 0
    this.totalReturns         = 0
    this.totalDamages         = 0
    this.totalDeficit         = 0
    this.totalDiscounts       = 0
    this.totalExpenditures    = 0
    this.totalBank            = 0
    this.totalCash            = 0
  }

  clearDetail(){
    this.detailId            = null
    this.barcode             = ''
    this.code                = ''
    this.description         = ''
    this.totalPacked         = 0
    this.qtySold             = 0
    this.qtyOffered          = 0
    this.qtyReturned         = 0
    this.qtyDamaged          = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0

    this.finalQty            = 0
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
    this.http.get<ISalesListDetail>(API_URL+'/sales_list_details/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.detailId = data!.id
        this.costPriceVatIncl = data!.costPriceVatIncl
        this.costPriceVatExcl = data!.costPriceVatExcl
        this.sellingPriceVatIncl = data!.sellingPriceVatIncl
        this.sellingPriceVatExcl = data!.sellingPriceVatExcl
        this.totalPacked         = data!.totalPacked
        this.qtySold             = data!.qtySold
        this.qtyOffered          = data!.qtyOffered
        this.qtyReturned         = data!.qtyReturned
        this.qtyDamaged          = data!.qtyDamaged
        this.costPriceVatIncl    = data!.costPriceVatIncl
        this.costPriceVatExcl    = data!.costPriceVatExcl
        this.sellingPriceVatIncl = data!.sellingPriceVatIncl
        this.sellingPriceVatExcl = data!.sellingPriceVatExcl
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
    await this.http.get<any>(API_URL+'/sales_lists/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        this.slstNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Packing List No request failed')
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
    this.disablePriceChange = true
  }

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title', size : 'xl'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  showChangeQty1(changeQty: any, id : any) {
    this.detailId = id
    this.modalService.open(changeQty, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  showChangeQty(changeQty : any, productId : string, detailId : string) {
     
    if(productId != ''){
      this.searchDetail(productId, detailId)
    }
    
    
    this.modalService.open(changeQty, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
    this.disablePriceChange = true
  }

  async change() {
    if(window.confirm("Are you sure you want to change qty for this product?")){
      //execute
    }else{
      //return
      return
    }
    let options = {
     headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
   }   
   var detail = {
     id                  : this.detailId,
     originalQty         : this.totalPacked,
     finalQty            : this.finalQty
   }
   this.spinner.show()
   await this.http.post(API_URL+'/sales_list_details/change', detail, options)
   .pipe(finalize(() => this.spinner.hide()))
   .toPromise()
   .then(
     () => {
       this.clearDetail()
       this.get(this.id)
       if(this.blank == true){
         this.blank = false
         this.loadSalesLists()
       }
     }
   )
   .catch(
     error => {
       console.log(error)
       ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save')
     }
   )
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
        alert('Could not load salesAgent names')
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
      },
      error => {
        console.log(error)
        alert('Could not load product descriptions')
      }
    )
  }

  async searchCustomer(name: string) {
    if(name == ''){
      this.customerId = ''
      this.customerNo = ''
      this.customerName = ''
      return
    }
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
    await this.http.get<ICustomer>(API_URL+'/sales_agents/get_by_alias?alias='+name, options)
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

  calculateTotalDeficit(){
    this.totalDeficit = +this.totalSales - (+this.totalExpenditures + +this.totalDiscounts + +this.totalBank + +this.totalCash)
  }

  enablePriceChange(){
    this.disablePriceChange = false
  }

  exportToPdf = () => {
    if(this.id == '' || this.id == null){
      return
    }
    var header = ''
    var footer = ''
    var title  = ''
    var logo : any = ''
    if(this.status == 'PENDING' || this.status == 'APPROVED' || this.status == 'CANCELED'){
      title = 'Sales List and Returns'
    }else{
      title = 'Sales and Returns'
    }
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9}, 
        {text : 'Price', fontSize : 9}, 
        {text : 'Total', fontSize : 9}, 
        {text : 'Sold_____', fontSize : 9}, 
        {text : 'Offered__', fontSize : 9}, 
        {text : 'Returned_', fontSize : 9}, 
        {text : 'Damaged__', fontSize : 9}
      ]
    ]   
    this.salesListDetails.forEach((element) => {
      var qtySold     : string = element.qtySold.toString()
      var qtyOffered  : string = element.qtyOffered.toString()
      var qtyReturned : string = element.qtyReturned.toString()
      var qtyDamaged  : string = element.qtyDamaged.toString()
      if(this.status == 'PENDING'){
        qtySold      = ''
        qtyOffered   = ''
        qtyReturned  = ''
        qtyDamaged   = ''
      }
      var detail = [
        {text : element.product.code.toString(), fontSize : 9}, 
        {text : element.product.description.toString(), fontSize : 9}, 
        {text : element.sellingPriceVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'},  
        {text : element.totalPacked.toString(), fontSize : 9}, 
        {text : qtySold, fontSize : 9}, 
        {text : qtyOffered, fontSize : 9}, 
        {text : qtyReturned, fontSize : 9}, 
        {text : qtyDamaged, fontSize : 9}
      ]
      report.push(detail)
    })

    var totalSales = this.totalSales.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalOffered = this.totalOffered.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalReturns = this.totalReturns.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalDamages = this.totalDamages.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalDiscounts = this.totalDiscounts.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalExpenditures = this.totalExpenditures.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalBank = this.totalBank.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalCash = this.totalCash.toLocaleString('en-US', { minimumFractionDigits: 2 })
    var totalDeficit = this.totalDeficit.toLocaleString('en-US', { minimumFractionDigits: 2 })

    if(this.status == 'PENDING'){
      totalSales        = ''
      totalOffered      = ''
      totalReturns      = ''
      totalDamages      = ''
      totalDiscounts    = ''
      totalExpenditures = ''
      totalBank         = ''
      totalCash         = ''
      totalDeficit      = ''
    }

    const docDefinition = {
      header: '',
      watermark : { text : this.companyName, color: 'blue', opacity: 0.1, bold: true, italics: false },
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
                  {text : 'Issue No', fontSize : 9}, 
                  {text : this.no, fontSize : 9} 
                ],
                [
                  {text : 'Issue Date', fontSize : 9}, 
                  {text : this.datepipe.transform(this.issueDate, 'yyyy-MM-dd') , fontSize : 9} 
                ],
                [
                  {text : 'Sales Officer', fontSize : 9}, 
                  {text : this.salesAgentName, fontSize : 9} 
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
          '  ',
          {
            table : {
                headerRows : 1,
                widths : ['auto', 'auto', 'auto', 'auto', 'auto', 'auto', 'auto', 'auto'],
                body : report
            }
        },
        ' ',
        ' ',
        {text : 'Summary', fontSize : 10, bold : true},
        {
          layout: 'lightHorizontalLines',
          table : {
            widths : [100, 200],
            body : [
              [
                {text : 'Issue No', fontSize : 9}, 
                {text : this.no, fontSize : 9} 
              ],
              [
                {text : 'Total Packed', fontSize : 9}, 
                {text : this.totalAmountPacked.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Sales', fontSize : 9}, 
                {text : totalSales, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Offer/Giveaway', fontSize : 9}, 
                {text : totalOffered, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Returns', fontSize : 9}, 
                {text : totalReturns, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Damages', fontSize : 9}, 
                {text : totalDamages, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Discounts', fontSize : 9}, 
                {text : totalDiscounts, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Expenditures', fontSize : 9}, 
                {text : totalExpenditures, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Bank', fontSize : 9}, 
                {text : totalBank, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Cash', fontSize : 9}, 
                {text : totalCash, fontSize : 9, alignment : 'right'} 
              ],
              [
                {text : 'Total Deficit', fontSize : 9}, 
                {text : totalDeficit, fontSize : 9, alignment : 'right'} 
              ]
            ]
          }         
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

interface ISalesList{
  id                 : any
  no                 : string
  issueDate          : Date
  customer           : ICustomer
  salesAgent         : ISalesAgent
  status             : string
  comments           : string
  created            : string
  approved           : string
  posted             : string
  salesListDetails : ISalesListDetail[]

  totalAmountPacked    : number
  totalSales           : number
  totalOffered         : number
  totalReturns         : number
  totalDamages         : number

  totalDiscounts       : number
  totalExpenditures     : number
  totalBank            : number
  totalCash            : number

  totalDeficit         : number
}

interface ISalesListDetail{
  id                  : any
  totalPacked         : number
  qtySold             : number
  qtyOffered          : number
  qtyReturned         : number
  qtyDamaged          : number   
  costPriceVatIncl    : number
  costPriceVatExcl    : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
  product             : IProduct
}

interface IProduct{
  id               : any
  barcode          : string
  code             : string
  description      : string
  packSize         : number
  costPriceVatIncl : number
  costPriceVatExcl : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number
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
}

interface ICustomerName{
  names : string[]
}

interface ISalesAgent{
  id                  : any
  no                  : string
  name                : string
}
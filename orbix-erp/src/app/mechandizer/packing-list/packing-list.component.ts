import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const pdfMakeX = require('pdfmake/build/pdfmake.js');
const pdfFontsX = require('pdfmake-unicode/dist/pdfmake-unicode.js');
pdfMakeX.vfs = pdfFontsX.pdfMake.vfs;
import * as pdfMake from 'pdfmake/build/pdfmake';
import { DataService } from 'src/app/services/data.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-packing-list',
  templateUrl: './packing-list.component.html',
  styleUrls: ['./packing-list.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class PackingListComponent implements OnInit {

  public pclNoLocked  : boolean = true
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
  salesListNo    : string
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
  posted         : string
  packingListDetails : IPackingListDetail[]
  packingLists       : IPackingList[]

  total            : number

  totalPreviousReturns : number
  totalAmountIssued    : number
  totalAmountPacked    : number

  customerNames  : string[] = []
  salesAgentNames  : string[] = []

  //detail
  detailId            : any
  barcode             : string
  productId           : any
  code                : string
  description         : string
  
  previousReturns     : number
  qtyIssued           : number
  totalPacked         : number  
  costPriceVatIncl    : number
  costPriceVatExcl    : number
  sellingPriceVatIncl : number
  sellingPriceVatExcl : number

  descriptions : string[]

  address : any
  companyName : string = ''

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id               = ''
    this.no               = ''
    this.salesListNo      = ''
    this.status           = ''
    this.comments         = ''
    this.created          = ''
    this.approved         = ''
    this.posted           = ''
    this.packingListDetails   = []
    this.packingLists         = []

    this.total            = 0

    this.totalPreviousReturns = 0
    this.totalAmountIssued    = 0
    this.totalAmountPacked    = 0

    this.detailId            = ''
    this.barcode             = ''
    this.code                = ''    
    this.description         = ''
    this.previousReturns     = 0
    this.qtyIssued           = 0
    this.totalPacked         = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0

    this.descriptions        = []
  }

  async ngOnInit(): Promise<void> {
    this.address = await this.data.getAddress2()
    this.logo = await this.data.getLogo()
    this.companyName = await this.data.getCompanyName()
    this.loadPackingLists()
    this.loadCustomerNames()
    this.loadSalesAgentNames()
    this.loadProductDescriptions()
  }

  async save() {   
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var packingList = {
      id           : this.id,
      customer     : {no : this.customerNo, name : this.customerName},
      salesAgent     : {no : this.salesAgentNo, alias : this.salesAgentName},
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){  
      this.spinner.show() 
      await this.http.post<IPackingList>(API_URL+'/packing_lists/create', packingList, options)
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
          this.posted               = data!.posted
          this.totalPreviousReturns = data!.totalPreviousReturns
          this.totalAmountIssued    = data!.totalAmountIssued
          this.totalAmountPacked    = data!.totalAmountPacked
          this.get(this.id)
          alert('Packing List created successifully')
          this.blank = true
          this.loadPackingLists()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Packing List')
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<IPackingList>(API_URL+'/packing_lists/update', packingList, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id                   = data?.id
          this.no                   = data!.no
          this.salesListNo          = data!.salesListNo
          this.status               = data!.status
          this.comments             = data!.comments
          this.created              = data!.created
          this.approved             = data!.approved
          this.posted               = data!.posted
          this.totalPreviousReturns = data!.totalPreviousReturns
          this.totalAmountIssued    = data!.totalAmountIssued
          this.totalAmountPacked    = data!.totalAmountPacked
          this.get(this.id)
          alert('Packing List updated successifully')
          this.loadPackingLists()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Packing List')
        }
      )
    }
  }

  refresh(){
    if(this.status == 'APPROVED'){
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
    await this.http.get<IPackingList>(API_URL+'/packing_lists/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id              = data?.id
        this.no              = data!.no
        this.salesListNo          = data!.salesListNo
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
        this.posted               = data!.posted
        this.totalPreviousReturns = data!.totalPreviousReturns
        this.totalAmountIssued    = data!.totalAmountIssued
        this.totalAmountPacked    = data!.totalAmountPacked
        this.refresh()
        this.packingListDetails   = data!.packingListDetails
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Packing List')
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
    await this.http.get<IPackingList>(API_URL+'/packing_lists/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id            = data?.id
        this.no            = data!.no 
        this.salesListNo          = data!.salesListNo
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
        this.posted               = data!.posted
        this.totalPreviousReturns = data!.totalPreviousReturns
        this.totalAmountIssued    = data!.totalAmountIssued
        this.totalAmountPacked    = data!.totalAmountPacked
        this.refresh()
        this.packingListDetails   = data!.packingListDetails
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Packing List')
      }
    )
  }

  async requestNo(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/packing_lists/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        this.pclNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Packing List No request failed')
      }
    )
  }

  async approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Packing List')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var pcl = {
      id                   : this.id,   
      totalPreviousReturns : this.totalPreviousReturns, 
      totalAmountIssued    : this.totalAmountIssued,   
      totalAmountPacked    : this.totalAmountPacked          
    }
    this.spinner.show()
    await this.http.put(API_URL+'/packing_lists/approve', pcl, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadPackingLists()
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

  async post(id: any) {
    if(!window.confirm('Confirm posting of the selected Packing List')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var pcl = {
      id                   : this.id,   
      totalPreviousReturns : this.totalPreviousReturns, 
      totalAmountIssued    : this.totalAmountIssued,   
      totalAmountPacked    : this.totalAmountPacked   
    }
    this.spinner.show()
    await this.http.put(API_URL+'/packing_lists/post', pcl, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadPackingLists()
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
    if(!window.confirm('Confirm canceling of the selected Packing List')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var pcl = {
      id : this.id   
    }
    this.spinner.show()
    await this.http.put(API_URL+'/packing_lists/cancel', pcl, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadPackingLists()
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
       * First Create a new Packing List
       */
      
      await this.save()
    }
    /**
     * Enter Packing List Detail
     */
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }   
    var detail = {
      packingList         : {id : this.id},
      product             : {id : this.productId, code : this.code},
      previousReturns     : this.previousReturns,
      qtyIssued           : this.qtyIssued,
      totalPacked         : this.totalPacked,  
      costPriceVatIncl    : this.costPriceVatIncl,
      costPriceVatExcl    : this.costPriceVatExcl,
      sellingPriceVatIncl : this.sellingPriceVatIncl,
      sellingPriceVatExcl : this.sellingPriceVatExcl
    }  
    if(this.costPriceVatExcl == 0 && this.costPriceVatIncl == 0 && this.sellingPriceVatExcl == 0 && this.sellingPriceVatIncl == 0){
      alert('Could not save detail, invalid price values')
      return
    }    
    this.spinner.show()
    await this.http.post(API_URL+'/packing_list_details/save', detail, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clearDetail()
        this.get(this.id)
        if(this.blank == true){
          this.blank = false
          //this.loadPackingLists()
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
    this.http.delete(API_URL+'/packing_list_details/delete?id='+id, options)
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

  loadPackingLists(){
    this.packingLists = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.http.get<IPackingList[]>(API_URL+'/packing_lists', options)
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.packingLists.push(element)
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
    var pcl = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/packing_lists/archive', pcl, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadPackingLists()
        alert('Packing List archived successifully')
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
    if(!window.confirm('Confirm archiving Packing Lists. All Posted  and debt free documents will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/packing_lists/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadPackingLists()
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
    this.pclNoLocked  = false
    this.inputsLocked = false
    this.valuesLocked = true   
  }

  lockAll(){
    this.pclNoLocked  = true
    this.inputsLocked = true
  }

  clear(){
    this.id                   = ''
    this.no                   = ''
    this.salesListNo = ''
    this.status               = ''
    this.comments             = ''
    this.created              = ''
    this.approved             = ''
    this.posted               = ''
    this.packingListDetails   = []
    this.customerNo           = ''
    this.customerName         = ''
    this.salesAgentNo           = ''
    this.salesAgentName         = ''
    this.totalPreviousReturns = 0
    this.totalAmountIssued    = 0
    this.totalAmountPacked    = 0
  }

  clearDetail(){
    this.detailId            = null
    this.barcode             = ''
    this.code                = ''
    this.description         = ''
    this.previousReturns     = 0
    this.qtyIssued           = 0
    this.totalPacked         = 0
    this.costPriceVatIncl    = 0
    this.costPriceVatExcl    = 0
    this.sellingPriceVatIncl = 0
    this.sellingPriceVatExcl = 0
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  async searchProduct(barcode : string, code : string, description : string){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    if(barcode != ''){
      //search by barcode
      this.spinner.show()
      await this.http.get<IProduct>(API_URL+'/products/get_by_barcode?barcode='+barcode, options)
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
      await this.http.get<IProduct>(API_URL+'/products/get_by_code?code='+code, options)
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
      await this.http.get<IProduct>(API_URL+'/products/get_by_description?description='+description, options)
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
    await this.http.get<IPackingListDetail>(API_URL+'/packing_list_details/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.detailId = data!.id
        this.previousReturns     = data!.previousReturns
        this.qtyIssued           = data!.qtyIssued
        this.totalPacked         = data!.totalPacked
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

  goToSalesList(no : string){
    if(no == '' || no == null){
      alert('Sales List not available')
      return
    }
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

  async calculateTotalPacked(){
    this.totalPacked =  +this.previousReturns + +this.qtyIssued
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
      title = 'Packing List'
    }else{
      title = 'Sales and Returns'
    }
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 500, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 500, y : 40}}
    }
    var report = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9}, 
        {text : 'Price', fontSize : 9}, 
        {text : 'Returns', fontSize : 9}, 
        {text : 'Issued', fontSize : 9}, 
        {text : 'Total', fontSize : 9} 
      ]
    ]   
    this.packingListDetails.forEach((element) => {
      var prevReturn : string = '-'
      var issued : string = '-'
      if(element.previousReturns != 0){
        prevReturn = element.previousReturns.toString()
      }
      if(element.qtyIssued != 0){
        issued = element.qtyIssued.toString()
      }
      var detail = [
        {text : element.product.code.toString(), fontSize : 9}, 
        {text : element.product.description.toString(), fontSize : 9}, 
        {text : element.sellingPriceVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'}, 
        {text : prevReturn, fontSize : 9}, 
        {text : issued.toString(), fontSize : 9}, 
        {text : element.totalPacked.toString(), fontSize : 9} 
      ]
      report.push(detail)
    })
    const docDefinition = {
      header: '',
      watermark : { text : this.companyName, color: 'blue', opacity: 0.1, bold: true, italics: false },
        content : [
          {
            columns : 
            [
              {
                width : 240,
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
                      ' '
                    ],
                    [
                      {text : 'Issue No     '+this.no, fontSize : 8}
                    ],
                    [
                      {text : 'Sales Agent  '+this.salesAgentName, fontSize : 8}
                    ],
                    [
                      {text : 'Customer     '+this.customerName, fontSize : 8}
                    ],
                    [
                      {text : 'Status       '+this.status, fontSize : 8}
                    ],
                  ]
                }
              },
              {
                width : 200,
                columns : [
                  this.address
                ]
              },
              logo
            ],
          },
          '  ',
          {
            table : {
                headerRows : 1,
                widths : [40, 250, 70, 'auto', 'auto', 'auto'],
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

interface IPackingList{
  id                 : any
  no                 : string
  salesListNo        : string
  customer           : ICustomer
  salesAgent           : ISalesAgent
  status             : string
  comments           : string
  created            : string
  approved           : string
  posted             : string
  packingListDetails : IPackingListDetail[]

  totalPreviousReturns : number
  totalAmountIssued    : number
  totalAmountPacked    : number
}
interface IPackingListDetail{
  id                  : any
  previousReturns     : number
  qtyIssued           : number
  totalPacked         : number   
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
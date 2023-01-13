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
import { trigger, state, style, transition, animate } from '@angular/animations';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-customer-claim',
  templateUrl: './customer-claim.component.html',
  styleUrls: ['./customer-claim.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class CustomerClaimComponent implements OnInit {
  closeResult    : string = ''

  logo!              : any
  address  : any 

  blank          : boolean = false

  
  id                       : any
  no                       : string
  status                   : string
  comments!                : string
  created                  : string
  approved                 : string
  customer!                : ICustomer
  customerId     : any
  customerNo!    : string
  customerName!  : string
  claimedProducts          : IClaimedProduct[]
  replacementProducts : IClaimReplacementProduct[]



  //initials
  claimedProductId                  : any
  claimedProductBarcode             : string
  claimedProductCode                : string
  claimedProductDescription         : string
  claimedProductQty                 : number
  claimedProductUom                 : string
  claimedProductCostPriceVatExcl    : number
  claimedProductCostPriceVatIncl    : number
  claimedProductSellingPriceVatExcl : number
  claimedProductSellingPriceVatIncl : number
  claimedProductReason : string
  claimedProductRemarks : string

  claimedDetailId : any
  


  //finals
  claimReplacementProductId                  : any
  claimReplacementProductBarcode             : string
  claimReplacementProductCode                : string
  claimReplacementProductDescription         : string
  claimReplacementProductQty                 : number
  claimReplacementProductUom                 : string    
  claimReplacementProductCostPriceVatExcl    : number
  claimReplacementProductCostPriceVatIncl    : number
  claimReplacementProductSellingPriceVatExcl : number
  claimReplacementProductSellingPriceVatIncl : number
  claimReplacementProductRemarks             : string

  replacementDetailId : any


  descriptions : string[]

  customerClaims : ICustomerClaim[]

  customerNames  : string[] = []

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id                       = null
    this.no                       = ''
    this.status                   = ''
    this.comments                 = ''
    this.created                  = ''
    this.approved                 = ''
    this.claimedProducts = []
    this.replacementProducts   = []

    this.claimedProductId                  = null
    this.claimedDetailId                   = ''
    this.claimedProductBarcode             = ''
    this.claimedProductCode                = ''  
    this.claimedProductDescription   = ''
    this.claimedProductQty                 = 0
    this.claimedProductUom                 = ''  
    this.claimedProductCostPriceVatExcl    = 0
    this.claimedProductCostPriceVatIncl    = 0
    this.claimedProductSellingPriceVatExcl = 0
    this.claimedProductSellingPriceVatIncl = 0
    this.claimedProductReason      = ''
    this.claimedProductRemarks     = ''



    this.claimReplacementProductId                  = null
    this.replacementDetailId                        = ''
    this.claimReplacementProductBarcode             = ''
    this.claimReplacementProductCode                = ''  
    this.claimReplacementProductDescription   = ''
    this.claimReplacementProductQty                 = 0
    this.claimReplacementProductUom                 = ''  
    this.claimReplacementProductCostPriceVatExcl    = 0
    this.claimReplacementProductCostPriceVatIncl    = 0
    this.claimReplacementProductSellingPriceVatExcl = 0
    this.claimReplacementProductSellingPriceVatIncl = 0
    this.claimReplacementProductRemarks = ''

  
    
    this.descriptions        = []
    this.customerClaims   = []
  }

  async ngOnInit(): Promise<void> {
    this.address = await this.data.getAddress()
    this.logo = await this.data.getLogo()
    this.loadClaims()
    this.loadProductDescriptions()
    this.loadCustomerNames()
  }
  
  async save() { 
    if(this.customerId == null || this.customerId == ''){
      alert('Please select customer')
      return
    } 
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var ccl = {
      id           : this.id,
      customer       : {id : this.customerId},
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){  
      this.spinner.show() 
      await this.http.post<ICustomerClaim>(API_URL+'/customer_claims/create', ccl, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no 
          this.status       = data!.status
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Claim Created successifully')
          this.blank = true
          this.loadClaims()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save claim')
          console.log(error)
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<ICustomerClaim>(API_URL+'/customer_claims/update', ccl, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no
          this.status       = data!.status
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Claim Updated successifully')
          this.loadClaims()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Claim')
        }
      )
    }
  }

  async get(id: any) {
    this.clear()
    this.clearClaimed()
    this.clearReplacement()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ICustomerClaim>(API_URL+'/customer_claims/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.id                       = data?.id
        this.no                       = data!.no
        this.status                   = data!.status
        this.customerId     = data!.customer.id
        this.customerNo     = data!.customer.no
        this.customerName   = data!.customer.name
        this.comments                 = data!.comments
        this.created                  = data!.created
        this.approved                 = data!.approved
        this.claimedProducts = data!.claimedProducts
        this.replacementProducts   = data!.replacementProducts
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Claim')
      }
    )
  }

  async getByNo(no: string) {
    this.clear()
    this.clearClaimed()
    this.clearReplacement()
    if(no == ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ICustomerClaim>(API_URL+'/customer_claims/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id                       = data?.id
        this.no                       = data!.no
        this.status                   = data!.status
        this.customerId     = data!.customer.id
        this.customerNo     = data!.customer.no
        this.customerName   = data!.customer.name
        this.comments                 = data!.comments
        this.created                  = data!.created
        this.approved                 = data!.approved
        this.claimedProducts = data!.claimedProducts
        this.replacementProducts   = data!.replacementProducts
        console.log(data)
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Claim')
      }
    )
  }

  async approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Claim')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var claim = {
      id : this.id   
    }
    this.spinner.show()
    await this.http.put(API_URL+'/customer_claims/approve', claim, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadClaims()
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
    if(!window.confirm('Confirm canceling of the selected Claim')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var claim = {
      id : this.id   
    }
    this.spinner.show()
    await this.http.put(API_URL+'/customer_claims/cancel', claim, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadClaims()
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
  
  async saveClaimedProduct() {
    
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Claim
       */
      alert('Claim not available, the system will create a new Claim')
      this.save()
    }else{
      /**
       * Enter Claim Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        id : this.claimedDetailId,
        customerClaim : {id : this.id},
        product : {id : this.claimedProductId},
        qty : this.claimedProductQty,
        reason : this.claimedProductReason,
        remarks : this.claimedProductRemarks
      }
      this.spinner.show()
      await this.http.post(API_URL+'/claimed_products/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearClaimed()
          this.clearReplacement()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadClaims()
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

  async saveReplacementProduct() {
    
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Claim
       */
      alert('Claim not available, the system will create a new Claim')
      this.save()
    }else{
      /**
       * Enter Claim Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        id : this.replacementDetailId,
        customerClaim : {id : this.id},
        product : {id : this.claimReplacementProductId},
        qty : this.claimReplacementProductQty,
        remarks : this.claimReplacementProductRemarks
      }
      this.spinner.show()
      await this.http.post(API_URL+'/claim_replacement_products/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearClaimed()
          this.clearReplacement()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadClaims()
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

  deleteClaimedProduct(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/claimed_products/delete?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.get(this.id)
      }
    )
    .catch(
      error => {ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not remove detail')
      }
    )
  }

  deleteFinal(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/claim_replacement_products/delete?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.get(this.id)
      }
    )
    .catch(
      error => {ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not remove detail')
      }
    )
  }

  loadClaims(){
    this.customerClaims = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<ICustomerClaim[]>(API_URL+'/customer_claims', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.customerClaims.push(element)
        })
      }
    )
  }

  async archive(id: any) {
    if(id == null || id == ''){
      window.alert('Please select Claim to archive')
      return
    }
    if(!window.confirm('Confirm archiving of the selected Claim')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/customer_claims/archive', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadClaims()
        alert('Claim archived successifully')
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
    if(!window.confirm('Confirm archiving Claim. All Approved conversions will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/customer_claims/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadClaims()
        alert('Claims archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  clear(){
    this.id                       = ''
    this.no                       = ''
    this.status                   = ''
    this.comments                 = ''
    this.created                  = ''
    this.approved                 = ''
    this.customerId = null
    this.customerName = ''
    this.customerNo = ''
    this.claimedProducts = []
    this.replacementProducts   = []
  }

  clearClaimed(){
    this.claimedProductId                  = null
    this.claimedDetailId                   = null
    this.claimedProductBarcode             = ''
    this.claimedProductCode                = ''  
    this.claimedProductDescription   = ''
    this.claimedProductQty                 = 0
    this.claimedProductUom                 = ''  
    this.claimedProductCostPriceVatExcl    = 0
    this.claimedProductCostPriceVatIncl    = 0
    this.claimedProductSellingPriceVatExcl = 0
    this.claimedProductSellingPriceVatIncl = 0
    this.claimedProductReason      = ''
    this.claimedProductRemarks     = ''
  }

  clearReplacement(){
    this.claimReplacementProductId                  = null
    this.replacementDetailId                        = null
    this.claimReplacementProductBarcode             = ''
    this.claimReplacementProductCode                = ''  
    this.claimReplacementProductDescription   = ''
    this.claimReplacementProductQty                 = 0
    this.claimReplacementProductUom                 = ''  
    this.claimReplacementProductCostPriceVatExcl    = 0
    this.claimReplacementProductCostPriceVatIncl    = 0
    this.claimReplacementProductSellingPriceVatExcl = 0
    this.claimReplacementProductSellingPriceVatIncl = 0
    this.claimReplacementProductRemarks = ''
  }

  async requestNo(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/customer_claims/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        //this.lpoNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Could not request CCL Number')
      }
    )
  }


  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  searchClaimedProduct(barcode : string, code : string, description : string){
    this.clearClaimed()
    this.clearReplacement()
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
          this.claimedProductId                  = data!.id
          this.claimedProductBarcode            = data!.barcode
          this.claimedProductCode           = data!.code
          this.claimedProductDescription         = data!.description
          this.claimedProductCostPriceVatExcl     = data!.costPriceVatExcl
          this.claimedProductCostPriceVatIncl     = data!.costPriceVatIncl
          this.claimedProductSellingPriceVatExcl  = data!.sellingPriceVatExcl
          this.claimedProductSellingPriceVatIncl  = data!.sellingPriceVatIncl

          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
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
          this.claimedProductId                  = data!.id
          this.claimedProductBarcode            = data!.barcode
          this.claimedProductCode           = data!.code
          this.claimedProductDescription         = data!.description
          this.claimedProductCostPriceVatExcl     = data!.costPriceVatExcl
          this.claimedProductCostPriceVatIncl     = data!.costPriceVatIncl
          this.claimedProductSellingPriceVatExcl  = data!.sellingPriceVatExcl
          this.claimedProductSellingPriceVatIncl  = data!.sellingPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
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
          this.claimedProductId                  = data!.id
          this.claimedProductBarcode            = data!.barcode
          this.claimedProductCode           = data!.code
          this.claimedProductDescription         = data!.description
          this.claimedProductCostPriceVatExcl     = data!.costPriceVatExcl
          this.claimedProductCostPriceVatIncl     = data!.costPriceVatIncl
          this.claimedProductSellingPriceVatExcl  = data!.sellingPriceVatExcl
          this.claimedProductSellingPriceVatIncl  = data!.sellingPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
        }
      )
      .catch(error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Product not found')
      })
    }
  }

  searchReplacementProduct(barcode : string, code : string, description : string){
    this.clearClaimed()
    this.clearReplacement()
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
          this.claimReplacementProductId                  = data!.id
          this.claimReplacementProductBarcode             = data!.barcode
          this.claimReplacementProductCode               = data!.code
          this.claimReplacementProductDescription          = data!.description
          this.claimReplacementProductCostPriceVatExcl    = data!.costPriceVatExcl
          this.claimReplacementProductCostPriceVatIncl     = data!.costPriceVatIncl
          this.claimReplacementProductSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.claimReplacementProductSellingPriceVatIncl  = data!.sellingPriceVatIncl

          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
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
          this.claimReplacementProductId                  = data!.id
          this.claimReplacementProductBarcode             = data!.barcode
          this.claimReplacementProductCode               = data!.code
          this.claimReplacementProductDescription          = data!.description
          this.claimReplacementProductCostPriceVatExcl    = data!.costPriceVatExcl
          this.claimReplacementProductCostPriceVatIncl     = data!.costPriceVatIncl
          this.claimReplacementProductSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.claimReplacementProductSellingPriceVatIncl  = data!.sellingPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
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
          this.claimReplacementProductId                  = data!.id
          this.claimReplacementProductBarcode             = data!.barcode
          this.claimReplacementProductCode               = data!.code
          this.claimReplacementProductDescription          = data!.description
          this.claimReplacementProductCostPriceVatExcl    = data!.costPriceVatExcl
          this.claimReplacementProductCostPriceVatIncl     = data!.costPriceVatIncl
          this.claimReplacementProductSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.claimReplacementProductSellingPriceVatIncl  = data!.sellingPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
        }
      )
      .catch(error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Product not found')
      })
    }
  }

  async searchClaimedProductDetail(detailId :any){ 
    this.clearClaimed()
    this.clearReplacement()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.get<IClaimedProduct>(API_URL+'/claimed_products/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.claimedDetailId = data!.id
        this.claimedProductId = data!.product.id
        this.claimedProductBarcode = data!.product.barcode
        this.claimedProductCode = data!.product.code
        this.claimedProductDescription = data!.product.description
        this.claimedProductQty = data!.qty
        this.claimedProductReason = data!.reason
        this.claimedProductRemarks = data!.remarks
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  openClaimed(contentClaimed : any, detailId : string) {
    if(detailId != ''){
      this.searchClaimedProductDetail( detailId)
    }
    
    this.modalService.open(contentClaimed, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  async searchClaimedReplacement(detailId :any){ 
    this.clearClaimed()
    this.clearReplacement()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.get<IClaimReplacementProduct>(API_URL+'/claim_replacement_products/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.replacementDetailId = data!.id
        this.claimReplacementProductId = data!.product.id
        this.claimReplacementProductBarcode = data!.product.barcode
        this.claimReplacementProductCode = data!.product.code
        this.claimReplacementProductDescription = data!.product.description
        this.claimReplacementProductQty = data!.qty
        this.claimReplacementProductRemarks = data!.remarks
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  openReplacement(contentReplacement : any, detailId : string) {
    if(detailId != ''){
      this.searchClaimedReplacement( detailId)
    }
    
    this.modalService.open(contentReplacement, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    this.clearClaimed()
    this.clearReplacement()
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
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load product descriptions')
      }
    )
  }

  

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
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


  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Customer Claim'
    var logo : any = ''
    var total : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var initialProduct = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9},
        {text : 'Qty', fontSize : 9},
      ]
    ]  
    var finalProduct = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9},
        {text : 'Qty', fontSize : 9},
      ]
    ] 
        
    this.claimedProducts.forEach((element) => {
      var detail = [
        {text : element.product.code.toString(), fontSize : 9}, 
        {text : element.product.description.toString(), fontSize : 9},
        {text : element.qty.toString(), fontSize : 9}, 
      ]
      initialProduct.push(detail)
    })

    this.replacementProducts.forEach((element) => {
      var detail = [
        {text : element.product.code.toString(), fontSize : 9}, 
        {text : element.product.description.toString(), fontSize : 9},
        {text : element.qty.toString(), fontSize : 9}, 
      ]
      finalProduct.push(detail)
    })
   
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
                  {text : 'Claim No', fontSize : 9}, 
                  {text : this.no, fontSize : 9} 
                ],
                [
                  {text : 'Customer Name', fontSize : 9}, 
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
          'Claimed Products',
          {
            table : {
              headerRows : 1,
              widths : ['auto', 200, 'auto'],
              body : 
                initialProduct
            }
        },
        ' ',
        'Replacement Products',
        {
          table : {
            headerRows : 1,
            widths : ['auto', 200, 'auto'],
            body : 
              finalProduct
          }
      },
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

interface ICustomerClaim{
  id           : any
  no           : string
  status       : string
  comments     : string
  created      : string
  approved     : string
  customer     : ICustomer
  claimedProducts : IClaimedProduct[]
  replacementProducts   : IClaimReplacementProduct[]
}



interface IClaimedProduct{
  id                  : any
  //barcode             : string
  //code                : string
  //description         : string
  //uom                 : string
  qty                 : number
  //costPriceVatExcl    : number
  //costPriceVatIncl    : number
  //sellingPriceVatExcl : number
  //sellingPriceVatIncl : number
  product             : IProduct
  reason    : string
  remarks : string
}

interface IClaimReplacementProduct{
  id                  : any
  //barcode             : string
  //code                : string
  //description         : string
  //uom                 : string
  qty                 : number
  //costPriceVatExcl    : number
  //costPriceVatIncl    : number
  //sellingPriceVatExcl : number
  //sellingPriceVatIncl : number
  product             : IProduct
  remarks : string
}

interface IProduct{
  id                  : any
  barcode             : string
  code                : string
  description         : string
  uom                 : string
  costPriceVatExcl    : number
  costPriceVatIncl    : number
  sellingPriceVatExcl : number
  sellingPriceVatIncl : number
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


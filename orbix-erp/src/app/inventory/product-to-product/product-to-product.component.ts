import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-product-to-product',
  templateUrl: './product-to-product.component.html',
  styleUrls: ['./product-to-product.component.scss']
})
export class ProductToProductComponent implements OnInit {
  closeResult    : string = ''

  blank          : boolean = false
  
  id                       : any
  no                       : string
  reason                   : string
  status                   : string
  comments!                : string
  created                  : string
  approved                 : string
  productToProductInitials : IInitialProduct[]
  productToProductFinals   : IFinalProduct[]



  //initials
  initialId                  : any
  initialBarcode             : string
  initialCode                : any
  initialDescription         : any
  initialQty                 : number
  initialUom                 : string
  initialCostPriceVatExcl    : number
  initialCostPriceVatIncl    : number
  initialSellingPriceVatExcl : number
  initialSellingPriceVatIncl : number
  


  //finals
  finalId                  : any
  finalBarcode             : string
  finalCode                : any
  finalDescription         : any
  finalQty                 : number
  finalUom                 : string    
  finalCostPriceVatExcl    : number
  finalCostPriceVatIncl    : number
  finalSellingPriceVatExcl : number
  finalSellingPriceVatIncl : number

  descriptions : string[]

  productToProducts : IProductToProduct[]

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private spinner: NgxSpinnerService) {
    this.id                       = null
    this.no                       = ''
    this.reason                   = ''
    this.status                   = ''
    this.comments                 = ''
    this.created                  = ''
    this.approved                 = ''
    this.productToProductInitials = []
    this.productToProductFinals   = []

    this.initialId                  = null
    this.initialBarcode             = ''
    this.initialCode                = ''  
    this.initialQty                 = 0
    this.initialUom                 = ''  
    this.initialCostPriceVatExcl    = 0
    this.initialCostPriceVatIncl    = 0
    this.initialSellingPriceVatExcl = 0
    this.initialSellingPriceVatIncl = 0

    this.finalId                  = null
    this.finalBarcode             = ''
    this.finalCode                = ''  
    this.finalQty                 = 0
    this.finalUom                 = ''  
    this.finalCostPriceVatExcl    = 0
    this.finalCostPriceVatIncl    = 0
    this.finalSellingPriceVatExcl = 0
    this.finalSellingPriceVatIncl = 0
    
    this.descriptions        = []
    this.productToProducts   = []
  }

  ngOnInit(): void {
    this.loadConversions()
    this.loadProductDescriptions()
  }
  
  async save() {  
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var product_to_product = {
      id           : this.id,
      reason       : this.reason,
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){  
      this.spinner.show() 
      await this.http.post<IProductToProduct>(API_URL+'/product_to_products/create', product_to_product, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no 
          this.reason       = data!.reason        
          this.status       = data!.status
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Conversion Created successifully')
          this.blank = true
          this.loadConversions()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Conversion')
          console.log(error)
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<IProductToProduct>(API_URL+'/product_to_products/update', product_to_product, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no
          this.reason       = data!.reason
          this.status       = data!.status
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Conversion Updated successifully')
          this.loadConversions()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Conversion')
        }
      )
    }
  }

  get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IProductToProduct>(API_URL+'/product_to_products/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id                       = data?.id
        this.no                       = data!.no
        this.reason                   = data!.reason
        this.status                   = data!.status
        this.comments                 = data!.comments
        this.created                  = data!.created
        this.approved                 = data!.approved
        this.productToProductInitials = data!.productToProductInitials
        this.productToProductFinals   = data!.productToProductFinals

        console.log(data)
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Conversion')
      }
    )
  }

  getByNo(no: string) {
    if(no == ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IProductToProduct>(API_URL+'/product_to_products/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id                       = data?.id
        this.no                       = data!.no
        this.reason                   = data!.reason
        this.status                   = data!.status
        this.comments                 = data!.comments
        this.created                  = data!.created
        this.approved                 = data!.approved
        this.productToProductInitials = data!.productToProductInitials
        this.productToProductFinals   = data!.productToProductFinals

        console.log(data)
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Conversion')
      }
    )
  }

  approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Conversion')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/product_to_products/approve', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadConversions()
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
    if(!window.confirm('Confirm canceling of the selected Conversion')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/product_to_products/cancel', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadConversions()
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
  
  async saveInitial() {
    
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Conversion
       */
      alert('Conversion not available, the system will create a new Conversion')
      this.save()
    }else{
      /**
       * Enter Conversion Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        id : this.initialId,
        productToProduct : {id : this.id},
        product : {id : this.initialId},
        qty : this.initialQty
      }
      this.spinner.show()
      await this.http.post(API_URL+'/product_to_product_initials/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearInitials()
          this.clearFinals()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadConversions()
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

  async saveFinal() {
    
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Conversion
       */
      alert('Conversion not available, the system will create a new Conversion')
      this.save()
    }else{
      /**
       * Enter Conversion Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        id : this.finalId,
        productToProduct : {id : this.id},
        product : {id : this.finalId},
        qty : this.finalQty
      }
      this.spinner.show()
      await this.http.post(API_URL+'/product_to_product_finals/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearInitials()
          this.clearFinals()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadConversions()
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

  deleteInitial(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/product_to_product_initials/delete?id='+id, options)
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
    this.http.delete(API_URL+'/product_to_product_finals/delete?id='+id, options)
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

  loadConversions(){
    this.productToProducts = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IProductToProduct[]>(API_URL+'/product_to_products', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.productToProducts.push(element)
        })
      }
    )
  }

  async archive(id: any) {
    if(id == null || id == ''){
      window.alert('Please select Conversion to archive')
      return
    }
    if(!window.confirm('Confirm archiving of the selected Conversion')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/product_to_products/archive', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadConversions()
        alert('Conversion archived successifully')
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
    if(!window.confirm('Confirm archiving Conversion. All Approved conversions will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/product_to_products/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadConversions()
        alert('Conversions archived successifully')
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
    this.reason                   = ''
    this.status                   = ''
    this.comments                 = ''
    this.created                  = ''
    this.approved                 = ''
    this.productToProductInitials = []
    this.productToProductFinals   = []
  }

  clearInitials(){
    this.initialId                  = null
    this.initialBarcode             = ''
    this.initialCode                = ''
    this.initialDescription         = ''  
    this.initialQty                 = 0
    this.initialUom                 = ''  
    this.initialCostPriceVatExcl    = 0
    this.initialCostPriceVatIncl    = 0
    this.initialSellingPriceVatExcl = 0
    this.initialSellingPriceVatIncl = 0
  }

  clearFinals(){
    this.finalId                  = null
    this.finalBarcode             = ''
    this.finalCode                = '' 
    this.finalDescription         = '' 
    this.finalQty                 = 0
    this.finalUom                 = ''  
    this.finalCostPriceVatExcl    = 0
    this.finalCostPriceVatIncl    = 0
    this.finalSellingPriceVatExcl = 0
    this.finalSellingPriceVatIncl = 0
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  searchInitialProduct(barcode : string, code : string, description : string){
    this.clearInitials()
    this.clearFinals()
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
          this.initialId                  = data!.id
          this.initialBarcode             = data!.barcode
          this.initialCode                = data!.code
          this.initialDescription         = data!.description
          this.initialCostPriceVatExcl    = data!.costPriceVatExcl
          this.initialCostPriceVatIncl    = data!.costPriceVatIncl
          this.initialSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.initialSellingPriceVatIncl = data!.sellingPriceVatIncl

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
          this.initialId                  = data!.id
          this.initialBarcode             = data!.barcode
          this.initialCode                = data!.code
          this.initialDescription         = data!.description
          this.initialCostPriceVatExcl    = data!.costPriceVatExcl
          this.initialCostPriceVatIncl    = data!.costPriceVatIncl
          this.initialSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.initialSellingPriceVatIncl = data!.sellingPriceVatIncl
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
          this.initialId                  = data!.id
          this.initialBarcode             = data!.barcode
          this.initialCode                = data!.code
          this.initialDescription         = data!.description
          this.initialCostPriceVatExcl    = data!.costPriceVatExcl
          this.initialCostPriceVatIncl    = data!.costPriceVatIncl
          this.initialSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.initialSellingPriceVatIncl = data!.sellingPriceVatIncl
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

  searchFinalProduct(barcode : string, code : string, description : string){
    this.clearInitials()
    this.clearFinals()
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
          this.finalId                  = data!.id
          this.finalBarcode             = data!.barcode
          this.finalCode                = data!.code
          this.finalDescription         = data!.description
          this.finalCostPriceVatExcl    = data!.costPriceVatExcl
          this.finalCostPriceVatIncl    = data!.costPriceVatIncl
          this.finalSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.finalSellingPriceVatIncl = data!.sellingPriceVatIncl

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
          this.finalId                  = data!.id
          this.finalBarcode             = data!.barcode
          this.finalCode                = data!.code
          this.finalDescription         = data!.description
          this.finalCostPriceVatExcl    = data!.costPriceVatExcl
          this.finalCostPriceVatIncl    = data!.costPriceVatIncl
          this.finalSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.finalSellingPriceVatIncl = data!.sellingPriceVatIncl
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
          this.finalId                  = data!.id
          this.finalBarcode             = data!.barcode
          this.finalCode                = data!.code
          this.finalDescription         = data!.description
          this.finalCostPriceVatExcl    = data!.costPriceVatExcl
          this.finalCostPriceVatIncl    = data!.costPriceVatIncl
          this.finalSellingPriceVatExcl = data!.sellingPriceVatExcl
          this.finalSellingPriceVatIncl = data!.sellingPriceVatIncl
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

  async searchInitialDetail(detailId :any){ 
    this.clearInitials()
    this.clearFinals()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.get<IInitialProduct>(API_URL+'/product_to_product_initials/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.initialId = data!.id
        this.initialBarcode = data!.product.barcode
        this.initialCode = data!.product.code
        this.initialDescription = data!.product.description
        this.initialQty = data!.qty
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  openInitial(contentInitial : any, detailId : string) {
    if(detailId != ''){
      this.searchInitialDetail( detailId)
    }
    
    this.modalService.open(contentInitial, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  async searchFinalDetail(detailId :any){ 
    this.clearInitials()
    this.clearFinals()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.get<IFinalProduct>(API_URL+'/product_to_product_finals/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.finalId = data!.id
        this.finalBarcode = data!.product.barcode
        this.finalCode = data!.product.code
        this.finalDescription = data!.product.description
        this.finalQty = data!.qty
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  openFinal(contentFinal : any, detailId : string) {
    if(detailId != ''){
      this.searchFinalDetail( detailId)
    }
    
    this.modalService.open(contentFinal, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    this.clearInitials()
    this.clearFinals()
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
}

interface IProductToProduct{
  id           : any
  no           : string
  status       : string
  reason       : string
  comments     : string
  created      : string
  approved     : string
  productToProductInitials : IInitialProduct[]
  productToProductFinals   : IFinalProduct[]
}



interface IInitialProduct{
  id                  : any
  barcode             : string
  code                : string
  description         : string
  uom                 : string
  qty                 : number
  costPriceVatExcl    : number
  costPriceVatIncl    : number
  sellingPriceVatExcl : number
  sellingPriceVatIncl : number
  product             : IProduct
}

interface IFinalProduct{
  id                  : any
  barcode             : string
  code                : string
  description         : string
  uom                 : string
  qty                 : number
  costPriceVatExcl    : number
  costPriceVatIncl    : number
  sellingPriceVatExcl : number
  sellingPriceVatIncl : number
  product             : IProduct
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


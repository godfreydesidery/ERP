import { Component, OnInit } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NgxSpinnerService } from 'ngx-spinner';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { DataService } from 'src/app/services/data.service';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-stock-card-report',
  templateUrl: './stock-card-report.component.html',
  styleUrls: ['./stock-card-report.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class StockCardReportComponent implements OnInit {
  logo!    : any
  address  : any 

  from! : Date
  to!   : Date

  descriptions : string[] = []
  products : IProduct[] = []

  closeResult    : string = ''

  supplierId    : any
  supplierCode  : string = ''
  supplierName  : string = ''

  supplierNames : string[] = []

  report : IProductStockCardReport[] = []


  id : any
  barcode : string = ''
  code : string = ''
  description : string = ''


  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
              private modalService: NgbModal,
              private spinner: NgxSpinnerService,
              private data : DataService) { }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
    this.loadSupplierNames()
    this.loadProductDescriptions()
  }

  async loadSupplierNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/suppliers/get_names', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.supplierNames = []
        data?.forEach(element => {
          this.supplierNames.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not load suppliers names')
      }
    )
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  async getProductStockCardReport(from: Date, to: Date, supplierName : string) {
    if(from == null || to == null){
      alert('Could not run report, please select date range')
      return
    }
    if(from > to){
      alert('Could not run report, invalid date range, final date must be later or same as the initial date')
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      from : from,
      to   : to,
      supplier : {
        name : supplierName
      },
      products : this.products
    }
    this.spinner.show()
    await this.http.post<IProductStockCardReport[]>(API_URL + '/reports/product_stock_card_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.report = data!
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load report')
      })
  }


  addProduct(){
    var product = {
      id : this.id,
      barcode : this.barcode,
      code : this.code,
      description : this.description
    }
    var exists = false
    this.products.forEach(element => {
      if(element.id == product.id){
        exists = true
      }
    })
    if(!exists){
      this.products.push(product)
    }
    this.clearProduct()
  }

  clearProduct(){
    this.id = null
    this.barcode = ''
    this.code = ''
    this.description = ''
  }

  removeProduct(id : any){
    /**Remove a single product from product list */
    var ps : IProduct[] = []
    this.products.forEach(element => {
      if(element.id != id){
        ps.push(element)
      }
    })
    this.products = ps
  }

  clearList(){
    this.clearProduct()
    this.products = []
  }

  clear(){
    this.report = []
  }

  showRunOptions(content: any) {
    
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }


  async search(){
    this.supplierName = ''   
    if(this.barcode != ''){
      await this.getByBarcode(this.barcode)
    }else if(this.code != ''){
      await this.getByCode(this.code)
    }else if(this.description != ''){
      await this.getByDescription(this.description)
    }else{
      alert('Please enter a search key')
    }
  }

  async getByBarcode(barcode: string): Promise<void> {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IProduct>(API_URL+'/products/get_by_barcode?barcode='+barcode, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id = data?.id
        this.barcode = data!.barcode
        this.code = data!.code
        this.description = data!.description
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested Product not found')
      }
    )
  }
  async getByCode(code: string): Promise<void> {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IProduct>(API_URL+'/products/get_by_code?code='+code, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id = data?.id
        this.barcode = data!.barcode
        this.code = data!.code
        this.description = data!.description
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested Product not found')
      }
    )
  }
  async getByDescription(description: string): Promise<void> {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IProduct>(API_URL+'/products/get_by_description?description='+description, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id = data?.id
        this.barcode = data!.barcode
        this.code = data!.code
        this.description = data!.description
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested Product not found')
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
        console.log(data)
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

  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Product Stock Cards Report'
    var logo : any = ''
    var total : number = 0
    var discount : number = 0
    var tax : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'Date', fontSize : 9, fillColor : '#bdc6c7'}, 
        {text : 'Code', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Description', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Qty In', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Qty Out', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Balance', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Reference', fontSize : 9, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      var detail = [
        {text : element.date.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.code.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.description.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.qtyIn.toString(), fontSize : 9, fillColor : '#ffffff'},
        {text : element.qtyOut.toString(), fontSize : 9, fillColor : '#ffffff'},
        {text : element.balance.toString(), fontSize : 9, fillColor : '#ffffff'},
        {text : element.reference.toString(), fontSize : 9, fillColor : '#ffffff'},
      ]
      report.push(detail)
    })
    const docDefinition = {
      header: '',
      watermark : { text : '', color: 'blue', opacity: 0.1, bold: true, italics: false },
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
                  {text : 'From', fontSize : 9}, 
                  {text : this.from, fontSize : 9} 
                ],
                [
                  {text : 'To', fontSize : 9}, 
                  {text : this.to, fontSize : 9} 
                ],
              ]
            },
          },
          '  ',
          {
            layout : 'noBorders',
            table : {
                headerRows : 1,
                widths : [50, 40, 140, 40, 40, 40, 100],
                body : report
            }
        },                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }
}

export interface IProductStockCardReport {
  date        : Date
  code        : string
  description : string
  qtyIn       : number
  qtyOut      : number
  balance     : number
  reference   : string
}


export interface IProduct {
  id : any
  barcode : string
  code : string
  description : string
}
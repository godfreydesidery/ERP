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
  selector: 'app-sales-sheet',
  templateUrl: './sales-sheet.component.html',
  styleUrls: ['./sales-sheet.component.scss']
})
export class SalesSheetComponent implements OnInit {
 

  public enableSearch : boolean = false

  logo!              : any 
  closeResult        : string = ''

  
  id             : any
  no             : string
  salesAgentName : string = ''
  confirmed : string = ''

  totalAmount : number = 0
  totalPaid : number = 0
  totalDiscount : number = 0
  totalCharges : number = 0
  totalDue : number = 0
 

  
  salesSheets       : ISalesSheet[]

  salesSheetSales : ISalesSheetSale[] = []


  

 

  customerNames  : string[] = []
  salesAgentNames  : string[] = []

  //detail


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

    this.salesSheets        = []

  

 

  }

  async ngOnInit(): Promise<void> {
    this.address = await this.data.getAddress()
    this.logo = await this.data.getLogo()
    this.companyName = await this.data.getCompanyName()
    this.loadSalesLists()

  }
  async get(id: any) {
    this.salesSheetSales = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesSheet>(API_URL+'/sales_sheets/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.id            = data?.id
        this.no            = data!.no 
        this.salesAgentName = data!.salesAgentName
        this.confirmed = data!.confirmed

        data?.salesSheetSales.forEach(element => {
          this.salesSheetSales.push(element)
        })
        this.refresh()       
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Sales Sheet')
      }
    )
  }

  async getByNo(no: string) {
    this.salesSheetSales = []
    if(no == ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesSheet>(API_URL+'/sales_sheets/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.id            = data?.id
        this.no            = data!.no 
        this.salesAgentName = data!.salesAgentName
        this.confirmed = data!.confirmed

        data?.salesSheetSales.forEach(element => {
          this.salesSheetSales.push(element)
        })
        this.refresh()
        
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Sales Sheet')
      }
    )
  }

  refresh(){
    this.totalAmount = 0
    this.totalPaid  = 0
    this.totalDiscount  = 0
    this.totalCharges  = 0
    this.totalDue  = 0
    this.salesSheetSales.forEach(element => {
      this.totalAmount = this.totalAmount + element.totalAmount
      this.totalPaid = this.totalPaid + element.totalPaid
      this.totalDiscount = this.totalDiscount + element.totalDiscount
      this.totalCharges = this.totalCharges + element.totalCharges
      this.totalDue = this.totalDue + element.totalDue
    })
  }

  

  

  

  loadSalesLists(){
    this.salesSheets = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.http.get<ISalesSheet[]>(API_URL+'/sales_sheets', options)
    .toPromise()
    .then(
      data => {
        console.log(data)
        data?.forEach(element => {
          this.salesSheets.push(element)
        })
      }
    ).catch(error => {console.log(error)})
  }

  

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

 

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title', size : 'xl'}).result.then((result) => {
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

  

  
  

  

  /**exportToPdf = () => {
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
    pdfMake.createPdf(docDefinition).open();**/


}

interface ISalesSheet{
  id : any
  no : string
  salesListNo : string
  salesAgentName : string
  confirmed : string
  salesSheetSales : ISalesSheetSale[]
}

interface ISalesSheetSale{
  id : any
  no : string
  customerName : string
  customerMobile : string
  customerLocation : string
  totalAmount : number
  totalPaid : number
  totalDiscount : number
  totalCharges : number
  totalDue : number
  //completedAt : string
  salesSheetSaleDetails : ISalesSheetSaleDetail[]	
}

interface ISalesSheetSaleDetail{
  id : any
  code : string
  barcode : string
  description : string
  qty : number
  price : number
}


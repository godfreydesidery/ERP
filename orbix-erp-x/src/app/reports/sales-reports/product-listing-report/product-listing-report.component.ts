import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { DataService } from 'src/app/services/data.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';
import { Workbook } from 'exceljs';
import { formatDate } from '@angular/common';
const fs = require('file-saver');

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-product-listing-report',
  templateUrl: './product-listing-report.component.html',
  styleUrls: ['./product-listing-report.component.scss']
})
export class ProductListingReportComponent implements OnInit {
  logo!    : any
  address  : any 

  from! : Date
  to!   : Date

  closeResult    : string = ''

  report : IProductListingReport[] = []

  totalAmount : number = 0
  totalDiscount : number = 0
  totalTax : number = 0

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
              private modalService: NgbModal,
              private spinner: NgxSpinnerService,
              private data : DataService) { }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  refresh(){
    this.totalAmount = 0
    this.totalDiscount = 0
    this.totalTax = 0
    this.report.forEach(element => {
      this.totalAmount = this.totalAmount + element.amount
    })
  }

  async getProductListingReport(from: Date, to: Date) {
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
      to   : to
    }
    this.spinner.show()
    await this.http.post<IProductListingReport[]>(API_URL + '/reports/product_listing_report', args, options)
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

  clear(){
    this.report = []
    this.refresh()
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

  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Product Listing Report'
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
        {text : 'Date', fontSize : 8, fillColor : '#bdc6c7'}, 
        {text : 'Code', fontSize : 8, fillColor : '#bdc6c7'}, 
        {text : 'Description', fontSize : 8, fillColor : '#bdc6c7'}, 
        {text : 'Qty', fontSize : 8, fillColor : '#bdc6c7'}, 
        {text : 'Amount', fontSize : 8, fillColor : '#bdc6c7'}, 
        {text : 'Cashier', fontSize : 8, fillColor : '#bdc6c7'}, 
        {text : 'Receipt#', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Invoice#', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Till#', fontSize : 8, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      if(element.cashier == null){
        element.cashier = ''
      }
      if(element.receiptNo == null){
        element.receiptNo = ''
      }
      if(element.invoiceNo == null){
        element.invoiceNo = ''
      }
      if(element.tillNo == null){
        element.tillNo = ''
      }
      var detail = [
        {text : formatDate(element.date, 'yyyy-MM-dd', 'en-US'), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.code.toString(), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.description.toString(), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.qty.toString(), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.amount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.cashier.toString(), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.receiptNo.toString(), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.invoiceNo.toString(), fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.tillNo.toString(), fontSize : 8, fillColor : '#ffffff'}, 
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
                widths : [50, 40, 140, 20, 50, 50, 50, 50, 50],
                body : report
            }
        },                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }


  async exportToSpreadsheet() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Product Listing Report')
   
    worksheet.columns = [
      { header: 'DATE', key: 'DATE'},
      { header: 'CODE', key: 'CODE'},
      { header: 'DESCRIPTION', key: 'DESCRIPTION'},
      { header: 'QTY', key: 'QTY'},
      { header: 'AMOUNT', key: 'AMOUNT'},
      { header: 'CASHIER', key: 'CASHIER'},
      { header: 'RECEIPT_NO', key: 'RECEIPT_NO'},
      { header: 'INVOICE_NO', key: 'INVOICE_NO'},
      { header: 'TILL_NO', key: 'TILL_NO'}
      
    ];
    this.spinner.show()
    this.report.forEach(element => {
      worksheet.addRow(
        {
          DATE        : formatDate(element.date, 'yyyy-MM-dd', 'en-US'),
          CODE        : element.code,
          DESCRIPTION : element.description,
          QTY         : element.qty,
          AMOUNT      : element.amount,
          CASHIER     : element.cashier,
          RECEIPT_NO  : element.receiptNo,
          INVOICE_NO  : element.invoiceNo,
          TILL_NO     : element.tillNo
        },"n"
      )
    })
    
    
    this.spinner.hide()
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Product Listing Report '+this.from+' to '+this.to+'.xlsx');
    })
   
  }



}

export interface IProductListingReport {
  date        : Date
  code        : string
  description : string
  qty         : number
  amount      : number
  cashier     : string
  receiptNo   : string
  invoiceNo   : string
  tillNo      : string
}
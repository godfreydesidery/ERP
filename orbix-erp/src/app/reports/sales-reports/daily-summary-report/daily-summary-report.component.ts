import { DatePipe } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Workbook } from 'exceljs';
import { NgxSpinnerService } from 'ngx-spinner';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { DataService } from 'src/app/services/data.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const fs = require('file-saver');


const API_URL = environment.apiUrl;

@Component({
  selector: 'app-daily-summary-report',
  templateUrl: './daily-summary-report.component.html',
  styleUrls: ['./daily-summary-report.component.scss']
})
export class DailySummaryReportComponent implements OnInit {
  logo!    : any
  address  : any 

  from! : Date
  to!   : Date

  report : IDailySummaryReport[] = []

  //totalPurchaseOnCash        : number = 0
  totalPurchaseOnCredit      : number = 0
  totalAmountPaid             : number = 0
  totalCashSales         : number = 0
  totalGrossMargin            : number = 0
  totalCreditSales            : number = 0

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
              private spinner: NgxSpinnerService,
              private data : DataService,
              private datePipe : DatePipe) { }

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
    //this.totalPurchaseOnCash         = 0
    this.totalPurchaseOnCredit       = 0
    this.totalAmountPaid              = 0
    this.totalCashSales          = 0
    this.totalGrossMargin             = 0
    this.totalCreditSales             = 0
    this.report.forEach(element => {
      //this.totalPurchaseOnCash         = this.totalPurchaseOnCash + element.purchaseOnCash
      this.totalPurchaseOnCredit       = this.totalPurchaseOnCredit + element.purchaseOnCredit
      this.totalAmountPaid              = this.totalAmountPaid + element.amountPaid
      this.totalCashSales          = this.totalCashSales + element.cashSales
      this.totalGrossMargin             = this.totalGrossMargin + element.grossMargin
      this.totalCreditSales             = this.totalCreditSales + element.creditSales
    })
  }


  async getDailySalesReport(from: Date, to: Date) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      from : from,
      to   : to
    }
    this.spinner.show()
    await this.http.post<IDailySummaryReport[]>(API_URL + '/reports/daily_summary_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          console.log(data)
          this.report = data!
          this.refresh()
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load report')
      })
  }

  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Daily Summary Report'
    var logo : any = ''
    var totalPurchaseOnCash   : number = 0
    var totalPurchaseOnCredit : number = 0
    var totalAmountPaid        : number = 0
    var totalCashSales         : number = 0
    var totalGrossMargin       : number = 0
    var totalCreditSales       : number = 0

    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'Date', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Opening Stock', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Purchase (Credit)', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Amount Paid', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Cash Sales', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Gross Margin', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Credit Sales', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Closing Stock', fontSize : 8, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      totalPurchaseOnCredit = totalPurchaseOnCredit + element.purchaseOnCredit
      totalAmountPaid       = totalAmountPaid + element.amountPaid
      totalCashSales        = totalCashSales + element.cashSales
      totalGrossMargin      = totalGrossMargin + element.grossMargin
      totalCreditSales      = totalCreditSales + element.creditSales

      var detail = [
        {text : element.date.toString() , fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.openingStockValue.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.purchaseOnCredit.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.amountPaid.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.cashSales.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.grossMargin.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.creditSales.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.closingStockValue.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
      ]
      report.push(detail)
    })
    var detailSummary = [
      {text : 'Total', fontSize : 8, fillColor : '#e1e6eb'}, 
      {text : '', fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalPurchaseOnCredit.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalAmountPaid.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalCashSales.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalGrossMargin.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalCreditSales.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : '',  fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
    ]
    report.push(detailSummary)
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
                widths : [50, 50, 50, 50, 50, 50, 50, 50],
                body : report
            }
        },                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }

  async exportToSpreadSheet() {
    this.spinner.show()
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Daily Summary Report From '+this.from +' to '+this.to)
   
    worksheet.columns = [
      { header: 'DATE', key: 'DATE'},
      { header: 'OPENING STOCK VALUE', key: 'OPENING_STOCK_VALUE'},
      { header: 'PURCHASE ON CASH', key: 'PURCHASE_ON_CASH'},
      { header: 'PURCHASE ON CREDIT', key: 'PURCHASE_ON_CREDIT'},
      { header: 'PAYMENT DATE', key: 'PAYMENT_DATE'},
      { header: 'AMOUNT PAID', key: 'AMOUNT_PAID'},
      { header: 'CASH SALES', key: 'CASH_SALES'},
      { header: 'GROSS MARGIN', key: 'GROSS_MARGIN'},
      { header: 'CREDIT SALES', key: 'CREDIT_SALES'},
      { header: 'CLOSING STOCK VALUE', key: 'CLOSING_STOCK_VALUE'},
      { header: 'STOCK TARGET', key: 'STOCK_TARGET'},
      { header: 'VARIATION IN STOCK TARGET', key: 'VARIATION_IN_STOCK_TARGET'}
    ];
    this.report.forEach(element => {
      worksheet.addRow(
        {
          DATE                      : this.datePipe.transform(element.date, 'yyyy-MM-dd'),
          OPENING_STOCK_VALUE       : element.openingStockValue,
          PURCHASE_ON_CASH          : '',
          PURCHASE_ON_CREDIT        : element.purchaseOnCredit,
          PAYMENT_DATE              : '',
          AMOUNT_PAID               : element.amountPaid,
          CASH_SALES                : element.cashSales,
          GROSS_MARGIN              : element.grossMargin,
          CREDIT_SALES              : element.creditSales,
          CLOSING_STOCK_VALUE       : element.closingStockValue,
          STOCK_TARGET              : '',
          VARIATION_IN_STOCK_TARGET : ''
        },"n"
      )
    })
    this.spinner.hide()
    
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Daily Summary Report From '+this.from +' to '+this.to+' .xlsx');
    })
   
  }

}

export interface IDailySummaryReport {
  date              : Date
  openingStockValue : number
  //purchaseOnCash    : number
  purchaseOnCredit  : number
  amountPaid        : number
  cashSales         : number
  grossMargin       : number
  creditSales       : number
  closingStockValue : number
  //stockTarget            : number
  //variationInStockTarget : number
}

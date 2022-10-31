import { DatePipe } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Workbook } from 'exceljs';
import { NgxSpinnerService } from 'ngx-spinner';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { DataService } from 'src/app/services/data.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';
import { formatDate } from '@angular/common';

const fs = require('file-saver');


const API_URL = environment.apiUrl;

@Component({
  selector: 'app-sales-and-purchases-summary-report',
  templateUrl: './sales-and-purchases-summary-report.component.html',
  styleUrls: ['./sales-and-purchases-summary-report.component.scss']
})
export class SalesAndPurchasesSummaryReportComponent implements OnInit {
  logo!    : any
  address  : any 

  from! : Date
  to!   : Date

  closeResult    : string = ''

  report : IDailyPurchaseAndSalesSummaryReport[] = []
  salesReport : ISalesSummaryReport[] = []
  purchaseReport : IPurchaseSummaryReport[] = []
  stockReport : IStockSummaryReport[] = []

  totalSalesVatExclTotal : number = 0
  totalSalesVatInclTotal : number = 0
  grossMarginTotal       : number = 0
  totalVatTotal          : number = 0
  totalPurchasesTotal    : number = 0

  //totalPurchaseOnCash        : number = 0
 // totalPurchaseOnCredit      : number = 0
  //totalAmountPaid             : number = 0
  //totalCashSales         : number = 0
  //totalGrossMargin            : number = 0
  //totalCreditSales            : number = 0

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
              private modalService: NgbModal,
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

  async refresh(){
    //this.totalPurchaseOnCash         = 0
    this.totalSalesVatExclTotal  = 0
    this.totalSalesVatInclTotal  = 0
    this.grossMarginTotal        = 0
    this.totalVatTotal           = 0
    this.totalPurchasesTotal     = 0
    this.report.forEach(element => {
      this.totalSalesVatExclTotal    = this.totalSalesVatExclTotal + element.totalSalesVatExcl
      this.totalSalesVatInclTotal    = this.totalSalesVatInclTotal + element.totalSalesVatIncl
      this.grossMarginTotal          = this.grossMarginTotal + element.grossMargin
      this.totalVatTotal             = this.totalVatTotal + element.totalVat
      this.totalPurchasesTotal       = this.totalPurchasesTotal + element.totalPurchases
    })
  }

  async getPurchaseAndSalesSummaryReport1(from: Date, to: Date) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      from : from,
      to   : to
    }
    this.spinner.show()
    await this.http.post<IDailyPurchaseAndSalesSummaryReport[]>(API_URL + '/reports/daily_summary_report', args, options)
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

  async getPurchaseAndSalesSummaryReport(from: Date, to: Date) {
    await this.getSalesReport(from, to)
    await this.getStockReport(from, to)
    await this.getPurchaseReport(from, to)

    this.report = []
   
    for(let i = 0; i < this.stockReport.length; i++ ){
      var rep : IDailyPurchaseAndSalesSummaryReport = {
        date : this.stockReport[i].date,
        openingStockValue : this.stockReport[i].openingStockValue,
        totalSalesVatExcl : this.salesReport[i].totalSalesVatExcl,
        totalSalesVatIncl : this.salesReport[i].totalSalesVatIncl,
        grossMargin : this.salesReport[i].grossMargin,
        totalVat : this.salesReport[i].totalVat,
        totalPurchases : this.purchaseReport[i].totalPurchases,
        closingStockValue : this.stockReport[i].closingStockValue
      }
      this.report.push(rep)
    }
    this.refresh() 
  }

  async getSalesReport(from: Date, to: Date) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      from : from,
      to   : to
    }
    this.spinner.show()
    await this.http.post<ISalesSummaryReport[]>(API_URL + '/reports/sales_summary_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          console.log(data)
          this.salesReport = data!
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load sales report')
      })
  }

  async getPurchaseReport(from: Date, to: Date) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      from : from,
      to   : to
    }
    this.spinner.show()
    await this.http.post<IPurchaseSummaryReport[]>(API_URL + '/reports/purchase_summary_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          console.log(data)
          this.purchaseReport = data!
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load purchase report')
      })
  }

  async getStockReport(from: Date, to: Date) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      from : from,
      to   : to
    }
    this.spinner.show()
    await this.http.post<IStockSummaryReport[]>(API_URL + '/reports/product_stock_summary_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          console.log(data)
          this.stockReport = data!
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load stock report')
      })
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

  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Purchase & Sales Summary Report'
    var logo : any = ''
    var totalSalesVatExcl : number = 0
    var totalSalesVatIncl : number = 0
    var grossMargin       : number = 0
    var totalVat          : number = 0
    var totalPurchases    : number = 0
    var totalCreditSales  : number = 0

    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'Date', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Opening Stock', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Total Purchases', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Total Sales(Excl)', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Total Sales(Incl)', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Gross Margin', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Total VAT', fontSize : 8, fillColor : '#bdc6c7'},
        {text : 'Closing Stock', fontSize : 8, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      totalSalesVatExcl = totalSalesVatExcl + element.totalSalesVatExcl
      totalSalesVatIncl = totalSalesVatIncl + element.totalSalesVatIncl
      grossMargin       = grossMargin + element.grossMargin
      totalVat          = totalVat + element.totalVat
      totalPurchases    = totalPurchases + element.totalPurchases
      

      var detail = [
        {text : formatDate(element.date, 'yyyy-MM-dd', 'en-US') , fontSize : 8, fillColor : '#ffffff'}, 
        {text : element.openingStockValue.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.totalPurchases.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.totalSalesVatExcl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.totalSalesVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.grossMargin.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.totalVat.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
        {text : element.closingStockValue.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#ffffff'},
      ]
      report.push(detail)
    })
    var detailSummary = [
      {text : 'Total', fontSize : 8, fillColor : '#e1e6eb'}, 
      {text : '', fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalPurchases.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalSalesVatExcl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalSalesVatIncl.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : grossMargin.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
      {text : totalVat.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 8, alignment : 'right', fillColor : '#e1e6eb'},
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
                widths : [50, 60, 60, 60, 60, 60, 50, 70],
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
      { header: 'TOTAL SALES(EXCL)', key: 'TOTAL_SALES_VAT_EXCL'},
      { header: 'TOTAL SALES(INCL)', key: 'TOTAL_SALES_VAT_INCL'},
      { header: 'TOTAL PURCHASES', key: 'TOTAL_PURCHASES'},
      { header: 'GROSS MARGIN', key: 'GROSS_MARGIN'},
      { header: 'TOTAL VAT', key: 'TOTAL_VAT'},
      { header: 'CLOSING STOCK VALUE', key: 'CLOSING_STOCK_VALUE'}
    ];
    this.report.forEach(element => {
      worksheet.addRow(
        {
          DATE                      : this.datePipe.transform(element.date, 'yyyy-MM-dd'),
          OPENING_STOCK_VALUE       : element.openingStockValue,
          TOTAL_PURCHASES          : element.totalPurchases,
          TOTAL_SALES_VAT_EXCL          : element.totalSalesVatExcl,
          TOTAL_SALES_VAT_INCL          : element.totalSalesVatIncl,
          GROSS_MARGIN              : element.grossMargin,
          TOTAL_VAT                 : element.totalVat,
          CLOSING_STOCK_VALUE       : element.closingStockValue,
          
        },"n"
      )
    })
    this.spinner.hide()
    
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Purchases and Sales Summary Report From '+this.from +' to '+this.to+' .xlsx');
    })
   
  }

}

export interface IDailyPurchaseAndSalesSummaryReport {
  date              : Date
  openingStockValue : number
  totalSalesVatExcl : number
  totalSalesVatIncl : number
  grossMargin : number
  totalVat : number
  totalPurchases : number
  closingStockValue : number
}

export interface IStockSummaryReport {
  date              : Date
  openingStockValue : number
  closingStockValue : number
}

export interface ISalesSummaryReport {
  date              : Date
  totalSalesVatExcl : number
  totalSalesVatIncl : number
  grossMargin : number
  totalVat : number
}


export interface IPurchaseSummaryReport {
  date              : Date
  totalPurchases : number
}


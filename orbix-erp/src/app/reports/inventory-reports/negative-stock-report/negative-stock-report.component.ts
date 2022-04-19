import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { Workbook } from 'exceljs';
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
  selector: 'app-negative-stock-report',
  templateUrl: './negative-stock-report.component.html',
  styleUrls: ['./negative-stock-report.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class NegativeStockReportComponent implements OnInit {
  logo!    : any
  address  : any 

  report : INegativeStockReport[] = []

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
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

  async getNegativeStockReport() {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.post<INegativeStockReport[]>(API_URL + '/reports/negative_stock_report', options)
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
  }

  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Negative Stock Report'
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
        {text : 'Code', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Description', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Qty', fontSize : 9, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      var detail = [
        {text : element.code.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.description.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.stock.toString(), fontSize : 9, fillColor : '#ffffff'},
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
                headerRows : 1,
                widths : [50, 300, 50],
                body : report
            }
        },                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }

  async exportToSpreadsheet() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Negative Stock Report')
   
    worksheet.columns = [
      { header: 'CODE', key: 'CODE'},
      { header: 'DESCRIPTION', key: 'DESCRIPTION'},
      { header: 'STOCK', key: 'STOCK'}
      
    ];
    this.spinner.show()
    this.report.forEach(element => {
      worksheet.addRow(
        {
          CODE         : element.code,
          DESCRIPTION  : element.description,
          STOCK        : element.stock
        },"n"
      )
    })
   
    

    this.spinner.hide()
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Negative Stock Report.xlsx');
    })
   
  }
}

export interface INegativeStockReport {
  code        : string
  description : string
  costPriceVatIncl : number
  sellingPriceVatIncl : number
  stock       : number
}
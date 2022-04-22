import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Workbook } from 'exceljs';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
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
  selector: 'app-supplier-stock-status',
  templateUrl: './supplier-stock-status.component.html',
  styleUrls: ['./supplier-stock-status.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class SupplierStockStatusComponent implements OnInit {
  logo!    : any
  address  : any 

  closeResult    : string = ''

  supplierNames : string[] = []
  supplierName : string = ''
  totalCost : number = 0
  totalValue : number = 0

  report : ISupplierStockStatusReport[] = []

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
              private modalService: NgbModal,
              private spinner: NgxSpinnerService,
              private data : DataService) { }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
    this.loadSupplierNames();
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  loadSupplierNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<string[]>(API_URL+'/suppliers/get_names', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.supplierNames.push(element)
        })
      }
    )
    .catch(
      error => {
        console.log(error)
      }
    )
  }

  async getSupplierStockStatusReport(name : string) {
    this.report = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    var args = {
      name : name
    }
    this.spinner.show()
    await this.http.post<ISupplierStockStatusReport[]>(API_URL + '/reports/supplier_stock_status_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        async data => {          
          this.report = data!
          await this.getTotal()
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load report')
      })
  }

  async getTotal(){
    this.totalCost = 0
    this.totalValue = 0
    this.report.forEach(element => {
      if(element.stock > 0){
        this.totalCost = this.totalCost + element.stock * element.costPriceVatIncl
        this.totalValue = this.totalValue + element.stock * element.sellingPriceVatIncl
      }
    })
  }

  clear(){
    this.report = []
    this.supplierName = ''
    this.totalCost    = 0
    this.totalValue   = 0
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
    var title  = 'Supplier Stock Status Report'
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
        {text : 'Stock Cost', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Stock Value', fontSize : 9, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      var detail = [
        {text : element.code.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.description.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.stock.toString(), fontSize : 9, fillColor : '#ffffff'},
        {text : (element.stock*element.costPriceVatIncl).toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#ffffff'},
        {text : (element.stock*element.sellingPriceVatIncl).toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#ffffff'},
      ]
      report.push(detail)
    })
    var summary = [
      {text : '', fontSize : 9, fillColor : '#bdc6c7'},
      {text : '', fontSize : 9, fillColor : '#bdc6c7'},
      {text : 'Total', fontSize : 9, fillColor : '#bdc6c7'},
      {text : this.totalCost.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#bdc6c7'},
      {text : this.totalValue.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#bdc6c7'},
      ]
    report.push(summary)
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
          {text : title, fontSize : 12, bold : true},
          '  ',
          'Supplier : ' + this.supplierName,
          '  ',
          {
            layout : 'noBorders',
            table : {
                headerRows : 1,
                widths : [50, 270, 30, 65, 65],
                body : report
            }
        },
        'NB: Negative stocks are excluded',                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }


  async exportToSpreadsheet() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Supplier Stock Status')
   
    worksheet.columns = [
      { header: 'CODE', key: 'CODE'},
      { header: 'DESCRIPTION', key: 'DESCRIPTION'},
      { header: 'STOCK', key: 'STOCK'},
      { header: 'TOTAL_COST', key: 'TOTAL_COST'},
      { header: 'TOTAL_VALUE', key: 'TOTAL_VALUE'}
    ];
    this.spinner.show()
    this.report.forEach(element => {
      worksheet.addRow(
        {
          CODE         : element.code,
          DESCRIPTION  : element.description,
          STOCK        : element.stock,
          TOTAL_COST   : element.costPriceVatIncl*element.stock,
          TOTAL_VALUE  : element.sellingPriceVatIncl*element.stock
        },"n"
      )
    })
    worksheet.addRow(
      {
        CODE         : '',
        DESCRIPTION  : '',
        STOCK        : 'Total',
        TOTAL_COST   : this.totalCost,
        TOTAL_VALUE  : this.totalValue
      },"n"
    )
    worksheet.addRow(
      {
        CODE         : 'NB: Negetive stocks are excluded',
        DESCRIPTION  : '',
        STOCK        : '',
        TOTAL_COST   : '',
        TOTAL_VALUE  : ''
      },"n"
    )

    this.spinner.hide()
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Supplier Stock Status.xlsx');
    })
   
  }



}

export interface ISupplierStockStatusReport {
  code        : string
  description : string
  costPriceVatIncl : number
  sellingPriceVatIncl : number
  stock       : number
}

import { trigger, state, style, transition, animate } from '@angular/animations';
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
import * as moment from 'moment';
const fs = require('file-saver');

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-material-usage-report',
  templateUrl: './material-usage-report.component.html',
  styleUrls: ['./material-usage-report.component.scss']
})
export class MaterialUsageReportComponent implements OnInit {
  logo!    : any
  address  : any 

  from!    : Date
  to!      : Date

  descriptions : string[] = []
  materials     : IMaterial[] = []

  closeResult  : string = ''

  report : IMaterialUsageReport[] = []
  displayReport : IMaterialUsageReport[] = []

  totalAmount   : number = 0
  totalDiscount : number = 0
  totalTax      : number = 0


  id                  : any
  barcode             : string = ''
  code                : string = ''
  description         : string = ''
  qty                 : number = 0
  


  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private spinner: NgxSpinnerService,
              private data : DataService) { }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
    this.loadMaterialDescriptions()
  }
  
  refresh(){
    this.totalAmount = 0
    this.report.forEach(element => {
      this.totalAmount = this.totalAmount + element.amount
    })
  }


  async getMaterialUsageReport(from: Date, to: Date) {
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
      materials : this.materials
    }
    this.spinner.show()
    await this.http.post<IMaterialUsageReport[]>(API_URL + '/reports/material_usage_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.report = data!
          this.refresh()
          this.displayReport = []
          var d1 : Date = new Date('0000,01,01')
          var d2 : Date = new Date('0000,01,01')
          this.report.forEach(element => {
            d2 = new Date(element.date)
            if((d2.valueOf() != d1.valueOf())){
              element.dateString = moment(new Date(element.date)).format('YYYY-MM-DD')
              d1 = d2
            }else{
              element.dateString = ''
            }
          })
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load report')
      })
  }

  addMaterial(){
    var material = {
      id          : this.id,
      barcode     : this.barcode,
      code        : this.code,
      description : this.description
    }
    var exists = false
    this.materials.forEach(element => {
      if(element.id == material.id){
        exists = true
      }
    })
    if(!exists){
      this.materials.push(material)
    }
    this.clearMaterial()
  }

  clearMaterial(){
    this.id          = null
    this.barcode     = ''
    this.code        = ''
    this.description = ''
  }

  removeMaterial(id : any){
    /**Remove a single material from material list */
    var ps : IMaterial[] = []
    this.materials.forEach(element => {
      if(element.id != id){
        ps.push(element)
      }
    })
    this.materials = ps
  }

  clearList(){
    this.clearMaterial()
    this.materials = []
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

  async search(){
    if(this.code != ''){
      await this.getByCode(this.code)
    }else if(this.description != ''){
      await this.getByDescription(this.description)
    }else{
      alert('Please enter a search key')
    }
  }

  
  async getByCode(code: string): Promise<void> {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IMaterial>(API_URL+'/materials/get_by_code?code='+code, options)
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
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested Material not found')
      }
    )
  }
  async getByDescription(description: string): Promise<void> {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IMaterial>(API_URL+'/materials/get_by_description?description='+description, options)
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
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested Material not found')
      }
    )
  }

  async loadMaterialDescriptions(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/materials/get_descriptions', options)
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
        alert('Could not load material descriptions')
      }
    )
  }


  exportToPdf = () => {
    var header = ''
    var footer = ''
    var supp = ''
    var title  = 'Material Usage Report'
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
        {text : 'Qty', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Amount', fontSize : 9, fillColor : '#bdc6c7'},
      ]
    ]    
    this.report.forEach((element) => {
      total = total + element.amount
      var detail = [
        {text : element.dateString.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.code.toString(), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.description.toString(), fontSize : 9, alignment : 'left', fillColor : '#ffffff'},
        {text : element.qty.toString(), fontSize : 9, alignment : 'left', fillColor : '#ffffff'},  
        {text : element.amount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#ffffff'},
      ]
      report.push(detail)
    })
    var detailSummary = [
      {text : '', fontSize : 9, alignment : 'left', fillColor : '#CCCCCC'}, 
      {text : '', fontSize : 9, alignment : 'left', fillColor : '#CCCCCC'},
      {text : '', fontSize : 9, alignment : 'left', fillColor : '#CCCCCC'},  
      {text : '', fontSize : 9, alignment : 'left', fillColor : '#CCCCCC'},
      {text : this.totalAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#CCCCCC'},        
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
                widths : [50,60, 270, 50, 70],
                body : report
            }
        },                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }


  async exportToSpreadsheet() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Material Usage Report')
   
    worksheet.columns = [
      { header: 'DATE', key: 'DATE'},
      { header: 'CODE', key: 'CODE'},
      { header: 'DESCRIPTION', key: 'DESCRIPTION'},
      { header: 'QTY', key: 'QTY'},
      { header: 'AMOUNT', key: 'AMOUNT'}
      
    ];
    this.spinner.show()
    this.report.forEach(element => {
      worksheet.addRow(
        {
          DATE         : element.dateString,
          CODE         : element.code,
          DESCRIPTION  : element.description,
          QTY          : element.qty,
          AMOUNT       : element.amount
        },"n"
      )
    })
    worksheet.addRow(
      {
        DATE         : '',
        CODE         : '',
        DESCRIPTION  : '',
        QTY          : 'Total',
        AMOUNT       : this.totalAmount
      },"n"
    )
    
    this.spinner.hide()
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Material Usage Report '+this.from+' to '+this.to+'.xlsx');
    }) 
  }
}

export interface IMaterialUsageReport {
  date                : string
  dateString          : string
  code                : string
  description         : string
  qty                 : number
  amount              : number
}

export interface IMaterial {
  id : any
  barcode : string
  code : string
  description : string
}
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { environment } from 'src/environments/environment';
import { NgxSpinnerService } from 'ngx-spinner';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs/operators';
import { Workbook } from 'exceljs';
import { formatDate } from '@angular/common';
import { isFakeTouchstartFromScreenReader } from '@angular/cdk/a11y';
import { DataService } from 'src/app/services/data.service';
const fs = require('file-saver');

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-debt-tracker',
  templateUrl: './debt-tracker.component.html',
  styleUrls: ['./debt-tracker.component.scss']
})
export class DebtTrackerComponent implements OnInit {

  logo!    : any
  address  : any 

  from! : Date
  to!   : Date

  id : any
  no : string
  inceptionDate! : Date
  totalAmount : number
  paid : number
  balance : number
  amountToPay : number
  status : string
  customer! : ICustomer
  officerIncharge! : ISalesAgent

  debtTrackers : IDebtTracker[] = [] 

  histories : IHistory[] = []

  totalAmountToPay   : number = 0
  totalPaid    : number = 0
  totalBalance : number = 0

  report : IDebtTrackerReport[] = []
  


  closeResult    : string = ''
  constructor(private auth : AuthService, 
              private http :HttpClient, 
              private modalService: NgbModal,
              private spinner : NgxSpinnerService,
              private data : DataService) {
    this.id = null
    this.no = ''
    this.totalAmount = 0
    this.paid = 0
    this.balance = 0
    this.amountToPay = 0
    this.status = ''
  }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
    this.getAll()
  }
  
  
  async getAll(): Promise<void> {
    this.totalBalance = 0;
    this.debtTrackers = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.auth.user.access_token)
    }
    this.spinner.show() 
    await this.http.get<IDebtTracker[]>(API_URL + '/debt_trackers', options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          console.log(data)
          data?.forEach(
            element => {
              this.debtTrackers.push(element)
              this.totalAmountToPay = this.totalAmountToPay + element.amount
              this.totalPaid = this.totalPaid + element.paid
              this.totalBalance = this.totalBalance + element.balance
            }
          )
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load debts')
      })
    return
  }

  async get(id : any){
    this.id = null
        this.no = ''
        this.status = ''
        this.totalAmount = 0
        this.paid = 0
        this.balance = 0
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.hide()
    await this.http.get<IDebtTracker>(API_URL+'/debt_trackers/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.id = data!.id
        this.no = data!.no
        this.status = data!.status
        this.totalAmount = data!.amount
        this.paid = data!.paid
        this.balance = data!.balance
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load debt')
    })
  }

  async pay() {
    if(this.amountToPay <= 0){
      alert("Invalid amount")
      return
    }
    if(!window.confirm('Pay the specified amount?')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var trc = {
      id : this.id,
      amount : this.amountToPay
        
    }
    this.spinner.show()
    await this.http.post(API_URL+'/debt_trackers/pay', trc, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        alert('Success')
        this.getAll()
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Operation failed')
      }
    )
  }



  openPay(contentPay : any, detailId : string) {
   this.amountToPay = 0
    this.modalService.open(contentPay, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  openHistory(history : any, detailId : string) {
   
    this.modalService.open(history, {ariaLabelledBy: 'modal-basic-title', size : 'xl'}).result.then((result) => {
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


  async getDebtTrackerReport(from: Date, to: Date) {
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
    this.debtTrackers = []
    this.totalAmountToPay = 0
    this.totalPaid = 0
    this.totalBalance = 0
    await this.http.post<IDebtTrackerReport[]>(API_URL + '/reports/debt_tracker_report', args, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          data?.forEach(element => {
            this.debtTrackers.push(
              {id : element.id, 
              no : element.no, 
              customer : {id : null, name : element.customerName, contactName : ''},
              officerIncharge : {id : '', no : '', name : element.officerIncharge},
              inceptionDay : {bussinessDate : element.inceptionDate},
              amount : element.totalAmount,
              paid : element.amountPaid,
              balance : element.balance,
              status : element.status
            })
            
          }
         
        )
        
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load')
      })
      this.debtTrackers.forEach(element => {
        this.totalAmountToPay = this.totalAmountToPay + element.amount
        this.totalPaid = this.totalPaid + element.paid
        this.totalBalance = this.totalBalance + element.balance
      })
  }

  async loadHistory(id : any) : Promise<void>{
    this.histories = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<IHistory[]>(API_URL+'/debt_trackers/history?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.histories!.push(element)
        })
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load history')
    })
  return
  }

  async archiveAll() {
    if(!window.confirm('Confirm archiving Paid Debts. All paid debts will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/debt_trackers/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.getAll()
        alert('All paid debts archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }


  showRunOptions(content: any) {
    
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }



  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Debt Tracker'
    var logo : any = ''
    var total : number = 0
    var totalPaid : number = 0
    var totalBalance : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var report = [
      [
        {text : 'No', fontSize : 9, fillColor : '#bdc6c7'}, 
        {text : 'Customer Name', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Officer Incharge', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Inception Date', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Total Amount', fontSize : 9, fillColor : '#bdc6c7'}, 
        {text : 'Amount Paid', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Balance', fontSize : 9, fillColor : '#bdc6c7'},
        {text : 'Status', fontSize : 9, fillColor : '#bdc6c7'},
      ]
    ]    
    this.debtTrackers.forEach((element) => {
      total = total + element.amount
      totalPaid = totalPaid + element.paid
      totalBalance = totalBalance + element.balance
      var detail = [
        {text : element.no, fontSize : 9, fillColor : '#ffffff'},
        {text : element.customer.name, fontSize : 9, fillColor : '#ffffff'},
        {text : element.officerIncharge.name, fontSize : 9, fillColor : '#ffffff'},
        {text : formatDate(element.inceptionDay.bussinessDate, 'yyyy-MM-dd', 'en-US'), fontSize : 9, fillColor : '#ffffff'}, 
        {text : element.amount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#ffffff'},
        {text : element.paid.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#ffffff'},  
        {text : element.balance.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#ffffff'},
        {text : element.status, fontSize : 9, fillColor : '#ffffff'},
      ]
      report.push(detail)
    })
    var detailSummary = [
      {text : '', fontSize : 9, fillColor : '#CCCCCC'},
      {text : '', fontSize : 9, fillColor : '#CCCCCC'},
      {text : '', fontSize : 9, fillColor : '#CCCCCC'},
      {text : '', fontSize : 9, fillColor : '#CCCCCC'}, 
      {text : total.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#CCCCCC'},
      {text : totalPaid.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#CCCCCC'},  
      {text : totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right', fillColor : '#CCCCCC'},
      {text : '', fontSize : 9, fillColor : '#CCCCCC'},
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
              {
                width : 200,
                layout : 'noBorders',
                table : {
                  widths : [220],
                  body : [
                    [
                      ' '
                    ],
                    [
                      {text : title, fontSize : 12, bold : true}
                    ],
                    
                    [
                      ''
                    ],
                    [
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
                    ]
                  ]
                }
              },
            ]
          },
          '  ',
          {
            layout : 'noBorders',
            table : {
                headerRows : 1,
                widths : [50, 60, 60, 50, 60, 60, 60, 50],
                body : report
            }
        },                   
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }



  async exportToSpreadsheet() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('Debt Tracker')
   
    worksheet.columns = [
      { header: 'NO', key: 'NO'},
      { header: 'CUSTOMER NAME', key: 'CUSTOMER'},
      { header: 'SALES AGENT', key: 'AGENT'},
      { header: 'INCEPTION DATE', key: 'DATE'},
      { header: 'TOTAL AMOUNT', key: 'AMOUNT'},
      { header: 'AMOUNT PAID', key: 'PAID'},
      { header: 'OUTSTANDING BALANCE', key: 'BALANCE'},
      { header: 'STATUS', key: 'STATUS'}
      
    ];
    this.spinner.show()
    this.debtTrackers.forEach(element => {
      worksheet.addRow(
        {
          NO    : element.no,
          CUSTOMER : element.customer.name,
          AGENT : element.officerIncharge.name,
          DATE      : formatDate(element.inceptionDay.bussinessDate, 'yyyy-MM-dd', 'en-US'),
          AMOUNT : element.amount,
          PAID : element.paid,
          BALANCE : element.balance,
          STATUS : element.status
        },"n"
      )
    })
    worksheet.addRow(
      {
        NO    : '',
        CUSTOMER : '',
        AGENT : '',
        DATE      : '',
        AMOUNT : this.totalAmountToPay,
        PAID : this.totalPaid,
        BALANCE : this.totalBalance,
        STATUS : ''
      },"n"
    )
    
    this.spinner.hide()
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'Debt Tracker '+this.from+' to '+this.to+'.xlsx');
    })
   
  }

  
}

export interface ICustomer {
  /**
   * Basic Inf
   */
  id          : any
  name        : string
  contactName : string 
  //active      : boolean 
  //tin         : string
  //vrn         : string
  /**
   * Credit Inf
   */
  //creditLimit  : number
  //invoiceLimit : number
  //creditDays   : number
  /**
   * Contact Inf
   */
  //physicalAddress : string
  //postCode        : string
  //postAddress     : string
  //telephone       : string
  //mobile          : string
  //email           : string
  //fax             : string
  /**
   * Bank Inf
   */
  //bankAccountName     : string
  //bankPhysicalAddress : string
  //bankPostAddress     : string
  //bankPostCode        : string
  //bankName            : string
  //bankAccountNo       : string
  //shippingAddress     : string
  //billingAddress      : string
}

export interface ISalesAgent {
  /**
   * Basic Inf
   */
  id         : any
  no : string
  name   : string
  //contactName   : string 
  //active : boolean 
  
  /**
   * Contract Inf
   */
  //termsOfContract : string
  /**
   * Credit Inf
   */
   //creditLimit  : number
   //invoiceLimit : number
   //creditDays   : number
   //salesTarget  : number
  /**
   * Contact Inf
   */
  //physicalAddress : string
  //postCode : string
  //postAddress : string
  //telephone : string
  //mobile : string
  //email : string
  //fax : string
}

export interface IDebtTracker{
  id : any
  no : string
  inceptionDay : IDay
  amount : number
  paid : number
  balance : number
  status : string
  customer : ICustomer
  officerIncharge : ISalesAgent
}

export interface IDay{
  bussinessDate : Date
}

export interface IHistory{
  day : IDay
  amount : number
  paid : number
  balance : number
  reference : string
}

export interface IDebtTrackerReport {
  id : any
  no : string
  customerName : string
  officerIncharge : string
  inceptionDate : Date
  totalAmount : number
  amountPaid : number
  balance : number
  status : string
}

import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { DataService } from 'src/app/services/data.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-debt-receipt',
  templateUrl: './debt-receipt.component.html',
  styleUrls: ['./debt-receipt.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class DebtReceiptComponent implements OnInit {

  public receiptNoLocked  : boolean = true
  public inputsLocked : boolean = true

  public enableSearch : boolean = false
  public enableDelete : boolean = false
  public enableSave   : boolean = false

  closeResult    : string = ''

  blank          : boolean = false

  logo!              : any
  address  : any 
  
  id             : any
  no             : string
  salesAgent!      : ISalesAgent
  salesAgentId     : any
  salesAgentNo!    : string
  salesAgentName!  : string
  status         : string
  mode           : string
  amount         : number
  chequeNo       : string
  comments!      : string
  created        : string
  approved       : string

  receipts       : IDebtReceipt[]
 
  salesAgentNames  : string[] = []

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id               = ''
    this.no               = ''
    this.status           = ''
    this.mode             = ''
    this.amount           = 0
    this.chequeNo         = ''
    this.comments         = ''
    this.created          = ''
    this.approved         = ''

    this.receipts         = []
    
  }

  async ngOnInit(): Promise<void> {
    this.logo = await this.data.getLogo() 
    this.address = await this.data.getAddress()
    this.loadReceipts()
    this.loadSalesAgentNames()
  }
  
  async save() {
    if(this.salesAgentId == null || this.salesAgentId == ''){
      alert('SalesAgent information missing')
      return
    }
    if(this.mode == ''){
      alert('Payment mode required')
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var debt_receipt = {
      id           : this.id,
      salesAgent     : {no : this.salesAgentNo, name : this.salesAgentName},
      mode         : this.mode,
      chequeNo     : this.chequeNo,
      amount       : this.amount,
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){ 
      this.spinner.show()  
      await this.http.post<IDebtReceipt>(API_URL+'/debt_receipts/create', debt_receipt, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no         
          this.status       = data!.status
          this.mode         = data!.mode
          this.amount       = data!.amount
          this.chequeNo     = data!.chequeNo
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Receipt Created successifully')
          this.blank = true
          this.loadReceipts()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Receipt')
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<IDebtReceipt>(API_URL+'/debt_receipts/update', debt_receipt, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no
          this.status       = data!.status
          this.mode         = data!.mode
          this.amount       = data!.amount
          this.chequeNo     = data!.chequeNo
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Receipt Updated successifully')
          this.loadReceipts()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Receipt')
        }
      )
    }
  }

  get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IDebtReceipt>(API_URL+'/debt_receipts/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.lockAll()
        this.id           = data?.id
        this.no           = data!.no
        this.salesAgentId   = data!.salesAgent.id
        this.salesAgentNo   = data!.salesAgent.no
        this.salesAgentName = data!.salesAgent.name
        this.status       = data!.status
        this.mode         = data!.mode
        this.amount       = data!.amount
        this.chequeNo     = data!.chequeNo
        this.comments     = data!.comments
        this.created      = data!.created
        this.approved     = data!.approved
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Receipt')
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
    this.http.get<IDebtReceipt>(API_URL+'/debt_receipts/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.lockAll()
        this.id           = data?.id
        this.no           = data!.no 
        this.salesAgentId   = data!.salesAgent.id
        this.salesAgentNo   = data!.salesAgent.no
        this.salesAgentName = data!.salesAgent.name 
        this.status       = data!.status
        this.mode         = data!.mode
        this.amount       = data!.amount
        this.chequeNo     = data!.chequeNo
        this.comments     = data!.comments
        this.created      = data!.created
        this.approved     = data!.approved
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Receipt')
      }
    )
  }

  async requestNo(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/debt_receipts/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        this.receiptNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Could not request Debt Number')
      }
    )
  }

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Receipt')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var receipt = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/debt_receipts/approve', receipt, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadReceipts()
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
    if(!window.confirm('Confirm canceling of the selected Receipt')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var receipt = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/debt_receipts/cancel', receipt, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadReceipts()
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
  
  getDetailByNo(no: string) {
    throw new Error('Method not implemented.');
  }

  loadReceipts(){
    this.receipts = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IDebtReceipt[]>(API_URL+'/debt_receipts', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        data?.forEach(element => {
          this.receipts.push(element)
        })
      }
    )
  }

  async archive(id: any) {
    if(id == null || id == ''){
      window.alert('Please select Receipt to archive')
      return
    }
    if(!window.confirm('Confirm archiving of the selected Receipt')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var receipt = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/debt_receipts/archive', receipt, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadReceipts()
        alert('Receipt archived successifully')
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
    if(!window.confirm('Confirm archiving Receipts. All PAID Receipts will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/debt_receipts/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadReceipts()
        alert('Receipts archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  unlockAll(){
    this.receiptNoLocked  = false
    this.inputsLocked = false   
  }

  lockAll(){
    this.receiptNoLocked  = true
    this.inputsLocked = true
  }

  clear(){
    this.id           = ''
    this.no           = ''
    this.status       = ''
    this.mode         = ''
    this.amount       = 0
    this.chequeNo     = ''
    this.comments     = ''
    this.created      = ''
    this.approved     = ''
    this.salesAgentNo = ''
    this.salesAgentName = ''
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  open(content: any, productId: string, detailId: string) {
    if (this.salesAgentNo == '' || this.salesAgentNo == null) {
      alert('Please enter salesAgent information')
      return
    }

    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {
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

  async loadSalesAgentNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/sales_agents/get_names', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.salesAgentNames = []
        data?.forEach(element => {
          this.salesAgentNames.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not load salesAgent names')
      }
    )
  }

  async searchSalesAgent(alias: string) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISalesAgent>(API_URL+'/sales_agents/get_by_name?name='+alias, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.salesAgentId = data?.id
        this.salesAgentNo = data!.no
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('SalesAgent not found')
        this.salesAgentId = ''
        this.salesAgentNo = ''
        this.salesAgentName = ''
      }
    )
  }

  exportToPdf = () => {
    if(this.id == '' || this.id == null){
      return
    }
    var header = ''
    var footer = ''
    var title  = 'Debt Receipt'
    var logo : any = ''
    var total : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    
    const docDefinition = {
      header: '',
      watermark : { text : title, color: 'blue', opacity: 0.1, bold: true, italics: false },
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
            widths : [75, 200],
            body : [
              [
                {text : 'Receipt No', fontSize : 9}, 
                {text : this.no, fontSize : 9} 
              ],
              [
                {text : 'Receipt Date', fontSize : 9}, 
                {text : '', fontSize : 9} 
              ],
              [
                {text : 'SalesAgent', fontSize : 9}, 
                {text : this.salesAgentName+'  ['+this.salesAgentNo+']', fontSize : 9} 
              ],
              [
                {text : 'Status', fontSize : 9}, 
                {text : this.status, fontSize : 9} 
              ],
              [
                {text : 'Mode of Payment', fontSize : 9}, 
                {text : this.mode, fontSize : 9} 
              ],
              [
                {text : 'Cheque No', fontSize : 9}, 
                {text : this.chequeNo, fontSize : 9} 
              ],
              [
                {text : 'Amount', fontSize : 9}, 
                {text : this.amount.toLocaleString('en-US', { minimumFractionDigits: 2 }), fontSize : 9, alignment : 'right'} 
              ],
            ]
          },
        },
      ]     
    };
    pdfMake.createPdf(docDefinition).open()
  }
}

interface IDebtReceipt{
  id           : any
  no           : string
  salesAgent     : ISalesAgent
  status       : string
  comments     : string
  receiptDate  : Date
  mode         : string
  amount       : number
  chequeNo     : string
  created      : string
  approved     : string
}

interface ISalesAgent{
  id                  : any
  no                  : string
  name                : string
  contactName         : string
  active              : boolean
  tin                 : string
  vrn                 : string
  creditLimit         : number
  invoiceLimit        : number
  creditDays          : number
  physicalAddress     : string
  postCode            : string
  postAddress         : string
  telephone           : string
  mobile              : string
  email               : string
  fax                 : string
  bankAccountName     : string
  bankPhysicalAddress : string
  bankPostAddress     : string
  bankPostCode        : string
  bankName            : string
  bankAccountNo       : string
}

interface ISalesAgentName{
  names : string[]
}
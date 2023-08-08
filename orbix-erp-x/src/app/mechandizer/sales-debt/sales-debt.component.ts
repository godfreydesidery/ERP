import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';




const pdfMakeX = require('pdfmake/build/pdfmake.js');
const pdfFontsX = require('pdfmake-unicode/dist/pdfmake-unicode.js');
pdfMakeX.vfs = pdfFontsX.pdfMake.vfs;


const API_URL = environment.apiUrl;

@Component({
  selector: 'app-sales-debt',
  templateUrl: './sales-debt.component.html',
  styleUrls: ['./sales-debt.component.scss']
})
export class SalesDebtComponent implements OnInit {
  closeResult    : string = ''

  blank          : boolean = false
  
  id             : any
  no           : string
  status : string
  amount : number
  balance : number
  amountToTransfer : number


  salesAgent!      : ISalesAgent
  salesAgentId     : any
  salesAgentNo!    : string
  salesAgentName!  : string
  salesAgentBalance! :number
  debts       : IDebt[]


  officerIncharge!      : ISalesAgent
  officerInchargeId     : any
  officerInchargeNo!    : string
  officerInchargeName!  : string

  customer!      : ICustomer
  customerId     : any
  customerNo!    : string
  customerName!  : string
  
  
  customerNames  : string[] = []
  salesAgentNames  : string[] = []
  officerInchargeNames  : string[] = []

  constructor(private auth : AuthService,
              private http :HttpClient,
              private modalService: NgbModal,
              private shortcut : ShortCutHandlerService,
              private spinner: NgxSpinnerService) {

    this.debts = []
    
    this.id      = null
    this.no      = ''
    this.status  = ''
    this.amount  = 0
    this.balance = 0
    this.amountToTransfer = 0
  }

  ngOnInit(): void {
    this.loadSalesAgentNames()
    this.loadCustomerNames()
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  async loadCustomerNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/customers/get_names', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.customerNames = []
        data?.forEach(element => {
          this.customerNames.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not load customer names')
      }
    )
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

  async searchCustomer(name: string) {
    if(name == ''){
      this.customerId = ''
      this.customerNo = ''
      this.customerName = ''
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ICustomer>(API_URL+'/customers/get_by_name?name='+name, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.customerId = data?.id
        this.customerNo = data!.no
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('Customer not found')
        this.customerId = ''
        this.customerNo = ''
        this.customerName = ''
      }
    )
  }

  async searchSalesAgent(name: string) {
    
    if (name == '') {
      this.salesAgentId = ''
      this.salesAgentNo = ''
      this.salesAgentBalance = 0
      this.debts = []
      return
    }

    this.salesAgentId = ''
    this.salesAgentNo = ''
    this.salesAgentBalance = 0
    this.debts = []

    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    this.spinner.show()
    await this.http.get<ISalesAgent>(API_URL+'/sales_agents/get_by_name?name='+name, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.salesAgentId = data?.id
        this.salesAgentNo = data!.no
        this.salesAgentBalance = data!.balance
        this.loadSalesAgentDebts(this.salesAgentId)
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

  clearNames(){
    this.customerId = ''
    this.customerNo = ''
    this.customerName = ''
    this.officerInchargeId = ''
    this.officerInchargeNo = ''
    this.officerInchargeName = ''
  }

  async searchOfficerIncharge(name: string) {
    if (name == '') {
      this.officerInchargeId = ''
      this.officerInchargeNo = ''
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    this.spinner.show()
    await this.http.get<ISalesAgent>(API_URL+'/sales_agents/get_by_name?name='+name, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data=>{
        this.officerInchargeId = data?.id
        this.officerInchargeNo = data!.no
      }
    )
    .catch(
      error=>{
        console.log(error)        
        alert('Name not found')
        this.officerInchargeId = ''
        this.officerInchargeNo = ''
        this.officerInchargeName = ''
      }
    )
  }

  async loadSalesAgentDebts(salesAgentId : any){
    this.debts = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.hide()
    await this.http.get<IDebt[]>(API_URL+'/debts/sales_agent?id='+salesAgentId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        data?.forEach(element => {
          this.debts.push(element)
        })
      }
    )
    .catch(error => {
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load debts')
    })
  }

  async get(id : any){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.hide()
    await this.http.get<IDebt>(API_URL+'/debts/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.id = data!.id
        this.no = data!.no
        this.status = data!.status
        this.amount = data!.amount
        this.balance = data!.balance
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load debt')
    })
  }

  async allocate(salesAgentId : any, debtId : any){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.post<boolean>(API_URL+'/debt_allocations/allocate?sales_agent_id='+salesAgentId+'&debt_id='+debtId, null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.searchSalesAgent(this.salesAgentName)
        alert('Allocated successifully')
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not allocate')
    })
  }

  open(content : any, productId : string, detailId : string) {
    this.clearNames()
    this.amountToTransfer = 0
    
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    this.clearDetail()
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
  clearDetail() {
    throw new Error('Method not implemented.');
  }

  async transfer() {
    if(this.customerName == ''){
      alert('Please select customer')
      return
    }
    if(this.officerInchargeName == ''){
      alert('Please select officer incharge')
      return
    }
    if(!window.confirm('Transfer the specified amount to debt tracker?')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var trc = {
      debt                   : {id : this.id},
      customer : {id : this.customerId},
      officerIncharge : {id : this.officerInchargeId}, 
      amount : this.amountToTransfer
        
    }
    this.spinner.show()
    await this.http.post(API_URL+'/debt_trackers/create', trc, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.searchSalesAgent(this.salesAgentName)
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load')
      }
    )
  }

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
  debtLimit        : number
  balance             : number
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


interface IDebt{
  id           : any
  no           : string
  salesAgent     : ISalesAgent
  status       : string
  comments     : string
  debtDate  : Date
  amount    : number
  balance      : number
  validUntil   : Date
  created      : string
  approved     : string
}

interface ICustomer{
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
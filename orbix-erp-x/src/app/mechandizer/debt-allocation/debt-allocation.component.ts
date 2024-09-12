import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-debt-allocation',
  templateUrl: './debt-allocation.component.html',
  styleUrls: ['./debt-allocation.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class DebtAllocationComponent implements OnInit {

  closeResult    : string = ''

  blank          : boolean = false
  
  id             : any
  salesAgent!      : ISalesAgent
  salesAgentId     : any
  salesAgentNo!    : string
  salesAgentName!  : string
  salesAgentBalance! :number
  debts       : IDebt[]
  
  salesAgentNames  : string[] = []

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService,
              private spinner: NgxSpinnerService) {

    this.debts = [] 
  }

  ngOnInit(): void {
    this.loadSalesAgentNames()
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
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

  async searchSalesAgent(name: string) {
    if (name == '') {
      this.salesAgentId = ''
      this.salesAgentNo = ''
      this.salesAgentBalance = 0
      this.debts = []
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
  balance      : number
  validUntil   : Date
  created      : string
  approved     : string
}

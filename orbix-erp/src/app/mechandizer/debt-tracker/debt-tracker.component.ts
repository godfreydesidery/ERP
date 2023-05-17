import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { environment } from 'src/environments/environment';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs/operators';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-debt-tracker',
  templateUrl: './debt-tracker.component.html',
  styleUrls: ['./debt-tracker.component.scss']
})
export class DebtTrackerComponent implements OnInit {

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


  closeResult    : string = ''
  constructor(private auth : AuthService, 
              private http :HttpClient, 
              private modalService: NgbModal,
              private spinner : NgxSpinnerService) {
    this.id = null
    this.no = ''
    this.totalAmount = 0
    this.paid = 0
    this.balance = 0
    this.amountToPay = 0
    this.status = ''
  }

  ngOnInit(): void {
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

  
}

export interface ICustomer {
  /**
   * Basic Inf
   */
  id          : any
  name        : string
  contactName : string 
  active      : boolean 
  tin         : string
  vrn         : string
  /**
   * Credit Inf
   */
  creditLimit  : number
  invoiceLimit : number
  creditDays   : number
  /**
   * Contact Inf
   */
  physicalAddress : string
  postCode        : string
  postAddress     : string
  telephone       : string
  mobile          : string
  email           : string
  fax             : string
  /**
   * Bank Inf
   */
  bankAccountName     : string
  bankPhysicalAddress : string
  bankPostAddress     : string
  bankPostCode        : string
  bankName            : string
  bankAccountNo       : string
  shippingAddress     : string
  billingAddress      : string
}

export interface ISalesAgent {
  /**
   * Basic Inf
   */
  id         : any
  no : string
  name   : string
  contactName   : string 
  active : boolean 
  
  /**
   * Contract Inf
   */
  termsOfContract : string
  /**
   * Credit Inf
   */
   creditLimit  : number
   invoiceLimit : number
   creditDays   : number
   salesTarget  : number
  /**
   * Contact Inf
   */
  physicalAddress : string
  postCode : string
  postAddress : string
  telephone : string
  mobile : string
  email : string
  fax : string
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

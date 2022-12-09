import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-sales-agent',
  templateUrl: './sales-agent.component.html',
  styleUrls: ['./sales-agent.component.scss']
})
export class SalesAgentComponent implements OnInit {
  public noLocked   : boolean = true
  public nameLocked   : boolean = true
  public inputsLocked : boolean = true

  public enableSearch : boolean = false
  public enableDelete : boolean = false
  public enableSave   : boolean = false

  id                  : any
  no                  : string
  name                : string
  contactName         : string
  active              : boolean
  tin                 : string
  vrn                 : string
  termsOfContract     : string
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

  salesAgents : ISalesAgent[] = []
  names     : string[] =[]

  constructor(private shortcut : ShortCutHandlerService, 
              private http : HttpClient, 
              private auth : AuthService, 
              private spinner : NgxSpinnerService) {
    this.id                  = ''
    this.no                = ''
    this.name                = ''
    this.contactName         = ''
    this.active              = true
    this.tin                 = ''
    this.vrn                 = ''
    this.termsOfContract     = ''
    this.physicalAddress     = ''
    this.postCode            = ''
    this.postAddress         = ''
    this.telephone           = ''
    this.mobile              = ''
    this.email               = ''
    this.fax                 = ''
    this.bankAccountName     = ''
    this.bankPhysicalAddress = ''
    this.bankPostAddress     = ''
    this.bankPostCode        = ''
    this.bankName            = ''
    this.bankAccountNo       = ''
  }
  ngOnInit(): void {
    this.getAll()
    this.loadSalesAgentNames()
  }

  async save() {
    /**
      * Create a single salesAgent
      */
    //validate inputs
    if(this.validateInputs() == false){
      return
    }

    var data = {
      id                  : this.id,
      no                  : this.no,
      name                : this.name,
      contactName         : this.contactName,
      active              : this.active,
      tin                 : this.tin,
      vrn                 : this.vrn,
      termsOfContract     : this.termsOfContract,
      physicalAddress     : this.physicalAddress,
      postCode            : this.postCode,
      postAddress         : this.postAddress,
      telephone           : this.telephone,
      mobile              : this.mobile,
      email               : this.email,
      fax                 : this.fax,
      bankAccountName     : this.bankAccountName,
      bankPhysicalAddress : this.bankPhysicalAddress,
      bankPostAddress     : this.bankPostAddress,
      bankPostCode        : this.bankPostCode,
      bankName            : this.bankName,
      bankAccountNo       : this.bankAccountNo
    }
    
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    if (this.id == null || this.id == ''){
      //create a new user
      this.spinner.show()
      await this.http.post(API_URL+'/sales_agents/create', data, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.lockAll()
          this.showSalesAgent(data)
          alert('SalesAgent created successifully')
          this.getAll()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not create salesAgent')
        }
      )   
    }else{
      //update an existing user
      this.spinner.show()
      await this.http.put(API_URL+'/sales_agents/update', data, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.lockAll()
          console.log(data)
          alert('SalesAgent updated successifully')
          this.getAll()
        }
      )
      .catch(
        error => {
          console.log(error);
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update salesAgent')
        }
      )   
    }
  }

  showSalesAgent(salesAgent : any){
    /**
     * Display customer details, takes a json customer object
     * Args: customer object
     */
    this.id                  = salesAgent['id']
    this.no                  = salesAgent['no']
    this.name                = salesAgent['name']
    this.contactName         = salesAgent['contactName']
    this.active              = salesAgent['active']
    this.tin                 = salesAgent['tin']
    this.vrn                 = salesAgent['vrn']
    this.termsOfContract     = salesAgent['termsOfContract']
    this.physicalAddress     = salesAgent['physicalAddress']
    this.postCode            = salesAgent['postCode']
    this.postAddress         = salesAgent['postAddress']
    this.telephone           = salesAgent['telephone']
    this.mobile              = salesAgent['mobile']
    this.email               = salesAgent['email']
    this.fax                 = salesAgent['fax']
    this.bankAccountName     = salesAgent['bankAccountName']
    this.bankPhysicalAddress = salesAgent['bankPhysicalAddress']
    this.bankPostAddress     = salesAgent['bankPostAddress']
    this.bankPostCode        = salesAgent['bankPostCode']
    this.bankName            = salesAgent['bankName']
    this.bankAccountNo       = salesAgent['bankAccountNo']
  }
  validateInputs() : boolean{
    let valid : boolean = true
    if(this.name == ''){
      alert('Empty name not allowed, please fill in the name field')
      return false
    }
    return valid
  }
  clearFields() {
    /**
     * Clear all the fields
     */
    this.id = ''
    this.no = ''
    this.name = ''
    this.contactName = ''
    this.tin = ''
    this.vrn = ''
    this.termsOfContract = ''
    this.physicalAddress = ''
    this.postCode = ''
    this.postAddress = ''
    this.telephone = ''
    this.mobile = ''
    this.email = ''
    this.fax = ''
    this.bankAccountName = ''
    this.bankPhysicalAddress = ''
    this.bankPostAddress = ''
    this.bankPostCode = ''
    this.bankName = ''
    this.bankAccountNo = ''
    this.unlockAll()
    if (this.id == null || this.id == '') {
      this.noLocked = true
    }
  }
  async getAll(): Promise<void> {
    this.salesAgents = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    await this.http.get<ISalesAgent[]>(API_URL+'/sales_agents', options)
    .toPromise()
    .then(
      data => {
        data?.forEach(
          element => {
            this.salesAgents.push(element)
          }
        )
      }
    )
    .catch(error => {
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load salesAgents')
    })
    return 
  }
  async get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    await this.http.get(API_URL+'/sales_agents/get?id='+id, options)
    .toPromise()
    .then(
      data=>{
        this.lockAll()
        this.showSalesAgent(data)
      }
    )
    .catch(
      error=>{
        console.log(error)        
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested record could not be found')
      }
    )
  }
  async getByNoOrName(no : string, name: string) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    if(no != ''){
      this.name = ''
    }
    if(no != ''){
      await this.http.get(API_URL+'/sales_agents/get_by_no?no='+no, options)
      .toPromise()
      .then(
        data=>{
          this.lockAll()
          this.showSalesAgent(data)
        }
      )
      .catch(
        error=>{
          console.log(error)        
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested record could not be found')
        }
      )
    }else{
      await this.http.get(API_URL+'/sales_agents/get_by_name?name='+name, options)
      .toPromise()
      .then(
        data=>{
          this.lockAll()
          this.showSalesAgent(data)
        }
      )
      .catch(
        error=>{
          console.log(error)        
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested record could not be found')
        }
      )
    }
  }
  async delete() {
    if(this.id == null || this.id == ''){
      alert('No salesAgent selected, please select a salesAgent to delete')
      return
    }
    if(!confirm('Confirm delete the selected salesAgent. This action can not be undone')){
      return
    }
    let options = {
      headers : new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.delete(API_URL+'/sales_agents/delete?id='+this.id, options)
    .toPromise()
    .then(
      () => {
        this.clearFields()
        this.getAll()
        alert('SalesAgent deleted succesifully')
        return true
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not delete salesAgent profile')
        return false
      }
    )
  }

  async loadSalesAgentNames(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get<string[]>(API_URL+'/sales_agents/get_names', options)
    .toPromise()
    .then(
      data => {
        this.names = []
        data?.forEach(element => {
          this.names.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not names')
      }
    )
  }

  unlockAll(){
    this.noLocked   = false 
    this.nameLocked   = false
    this.inputsLocked = false
  }

  lockAll(){
    this.noLocked   = true
    this.nameLocked   = true
    this.inputsLocked = true
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }


  async requestNo(){
    if(this.no != ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/sales_agents/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
      },
      error => {
        console.log(error)
        alert('Could not request Agent No')
      }
    )
  }

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
  tin : string
  vrn : string
  /**
   * Contract Inf
   */
  termsOfContract : string
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
  /**
   * Bank Inf
   */
  bankAccountName : string
  bankPhysicalAddress : string
  bankPostAddress : string
  bankPostCode : string
  bankName : string
  bankAccountNo : string

  save() : void
  getAll() : void
  get(id : any) : any
  getByNoOrName(no : string, name : string) : any
  delete() : any
}
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
  selector: 'app-vat-groups',
  templateUrl: './vat-groups.component.html',
  styleUrls: ['./vat-groups.component.scss']
})
export class VatGroupsComponent implements OnInit {
  public codeLocked   : boolean = true
  public nameLocked   : boolean = true
  public inputsLocked : boolean = true

  public enableSearch : boolean = false
  public enableDelete : boolean = false
  public enableSave   : boolean = false

  id                  : any
  code                : string
  value               : number
  description         : string
  

  vatGroups : IVatGroup[] = []
  codes     : string[]    = []

  constructor(private shortcut : ShortCutHandlerService, 
              private http : HttpClient, 
              private auth : AuthService, 
              private spinner : NgxSpinnerService) {
    this.id                  = ''
    this.code                = ''
    this.value               = 0
    this.description         = ''
  }
  ngOnInit(): void {
    this.getAll()
    this.loadCodes()
  }

  async save() {
   
    var data = {
      id                  : this.id,
      code                : this.code,
      value                : this.value,
      description         : this.description
    }
    
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    if (this.id == null || this.id == ''){
      //create a new user
      this.spinner.show()
      await this.http.post(API_URL+'/vat_groups/create', data, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.lockAll()
          this.showVatGroup(data)
          alert('VatGroup created successifully')
          this.getAll()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not create vat_group')
        }
      )   
    }else{
      //update an existing user
      this.spinner.show()
      await this.http.put(API_URL+'/vat_groups/update', data, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.lockAll()
          console.log(data)
          alert('VatGroup updated successifully')
          this.getAll()
        }
      )
      .catch(
        error => {
          console.log(error);
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update vat_group')
        }
      )   
    }
  }

  showVatGroup(vatGroup : any){
    /**
     * Display customer details, takes a json customer object
     * Args: customer object
     */
    this.id          = vatGroup['id']
    this.code        = vatGroup['code']
    this.value       = vatGroup['value']
    this.description = vatGroup['description']
  }
  
  clearFields() {
    /**
     * Clear all the fields
     */
    this.id = ''
    this.code = ''
    this.value = 0
    this.description = ''
    this.unlockAll()
    if (this.id == null || this.id == '') {
      this.codeLocked = true
    }
  }
  async getAll(): Promise<void> {
    this.vatGroups = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    await this.http.get<IVatGroup[]>(API_URL+'/vat_groups', options)
    .toPromise()
    .then(
      data => {
        data?.forEach(
          element => {
            this.vatGroups.push(element)
          }
        )
      }
    )
    .catch(error => {
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load vat groups')
    })
    return 
  }
  async get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }

    await this.http.get(API_URL+'/vat_groups/get?id='+id, options)
    .toPromise()
    .then(
      data=>{
        this.lockAll()
        this.showVatGroup(data)
      }
    )
    .catch(
      error=>{
        console.log(error)        
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested record could not be found')
      }
    )
  }
  async getByCode(code : string) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get(API_URL+'/vat_groups/get_by_code?code='+code, options)
      .toPromise()
      .then(
        data=>{
          this.lockAll()
          this.showVatGroup(data)
        }
      )
      .catch(
        error=>{
          console.log(error)        
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Requested record could not be found')
        }
      )
    
  }
  async delete() {
    if(this.id == null || this.id == ''){
      alert('No VAT group selected, please select a VAT group to delete')
      return
    }
    if(!confirm('Confirm delete the selected VAT group. This action can not be undone')){
      return
    }
    let options = {
      headers : new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.delete(API_URL+'/vat_groups/delete?id='+this.id, options)
    .toPromise()
    .then(
      () => {
        this.clearFields()
        this.getAll()
        alert('VAT group deleted succesifully')
        return true
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not delete VAT group')
        return false
      }
    )
  }

  async loadCodes(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get<string[]>(API_URL+'/vat_groups/get_codes', options)
    .toPromise()
    .then(
      data => {
        this.codes = []
        data?.forEach(element => {
          this.codes.push(element)
        })
      },
      error => {
        console.log(error)
        alert('Could not load VAT group codes')
      }
    )
  }

  unlockAll(){
    this.codeLocked   = false 
    this.nameLocked   = false
    this.inputsLocked = false
  }

  lockAll(){
    this.codeLocked   = true
    this.nameLocked   = true
    this.inputsLocked = true
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }


  async requestValue(){
    if(this.code != ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/vat_groups/request_value', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.value = data!['value']
      },
      error => {
        console.log(error)
        alert('Could not request VAT group value')
      }
    )
  }

}

export interface IVatGroup {
  id          : any
  code        : string
  value       : number
  description : string   
}
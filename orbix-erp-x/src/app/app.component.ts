import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AfterContentInit, AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from './../environments/environment';
import { AuthService } from './auth.service';
import { trigger,state,style,animate,transition} from '@angular/animations'; 
import { Observable } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { Title } from '@angular/platform-browser';

const API_URL = environment.apiUrl;

declare var myExtObject: any;
declare var webGlObject: any;

declare var apiUrl: any;
interface AppState{
  message : string
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = environment.projectName

  isLoggedIn : boolean = true;

  userName : string = ''


  public systemDate: string = ''


  companyName = ''


  mainMenuShown               : boolean = true
  adminMenuShown              : boolean = false
  inventoryMenuShown          : boolean = false
  mechandizerMenuShown        : boolean = false
  productionMenuShown         : boolean = false
  supplierRelationsMenuShown  : boolean = false
  customerRelationsMenuShown  : boolean = false
  humanResourceMenuShown      : boolean = false
  reportMenuShown             : boolean = false
  dayMenuShown                : boolean = false

  constructor(private http  : HttpClient,
    private auth : AuthService,
    private router: Router,
    private titleService: Title,
    private spinner: NgxSpinnerService){   
  }
   
  async ngOnInit(): Promise<void> {
    this.ping()
    this.loadCompanyName()
    this.titleService.setTitle('Orbix ERP - '+localStorage.getItem('company-name'))
    try{
      await this.loadDay()
    }catch(e:any){}    
    var currentUser = null
    if(localStorage.getItem('current-user') != null){
      currentUser = localStorage.getItem('current-user')
    }
    if(currentUser != null){
      localStorage.setItem('logged-in', 'true')
      this.isLoggedIn = true
      await this.router.navigate([''])
    }else{
      localStorage.setItem('logged-in', 'false')
      this.isLoggedIn = false
      await this.router.navigate([''])
    }

    if(localStorage.getItem('user-name') != null){
      this.userName = localStorage.getItem('user-name')!
    }else{
      this.userName = ''
    }
    if(localStorage.getItem('system-date') != null){
      this.systemDate = localStorage.getItem('system-date')!
    }else{
      this.systemDate = ''
    } 
    this.companyName = localStorage.getItem('company-name') + '' 


  }

  async ping() {   
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get(API_URL+'/ping', options)
    .toPromise()
    .then(
      ()=>{}
    )
    .catch(
      ()=>{}
    )
  }

  async loadDay(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get<IDayData>(API_URL+'/days/get_bussiness_date', options)
    .toPromise()
    .then(
      data => {
        localStorage.setItem('system-date', data?.bussinessDate!+'')        
      }
    )
    .catch(error => {})
  }

  async loadCompanyName(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get<ICompanyData>(API_URL+'/company_profile/get', options)
    .toPromise()
    .then(
      data => {
        localStorage.setItem('company-name', data?.companyName!+'')        
      }
    )
    .catch(error => {})
  }

  public async logout() : Promise<any>{
    if(window.confirm('Log out?')){
      localStorage.removeItem('current-user')
      alert('You have logged out!')
      await this.router.navigate([''])
      window.location.reload()
    }
  }




  hideAll(){
    this.mainMenuShown = false
    this.adminMenuShown = false
    this.inventoryMenuShown = false
    this.mechandizerMenuShown = false
    this.productionMenuShown = false
    this.supplierRelationsMenuShown = false
    this.customerRelationsMenuShown = false
    this.humanResourceMenuShown = false
    this.reportMenuShown = false
    this.dayMenuShown = false
  }

  showMainMenu(){
    this.hideAll()
    this.mainMenuShown = true
  }
  showAdminMenu(){
    this.hideAll()
    this.adminMenuShown = true
  } 
  showInventoryMenu(){
    this.hideAll()
    this.inventoryMenuShown = true
  } 

  showMechandizerMenu(){
    this.hideAll()
    this.mechandizerMenuShown = true
  } 

  showProductionMenu(){
    this.hideAll()
    this.productionMenuShown = true
  } 

  showSupplierRelationsMenu(){
    this.hideAll()
    this.supplierRelationsMenuShown = true
  } 

  showCustomerRelationsMenu(){
    this.hideAll()
    this.customerRelationsMenuShown = true
  } 

  showHumanResourceMenu(){
    this.hideAll()
    this.humanResourceMenuShown = true
  } 

  showReportMenu(){
    this.hideAll()
    this.reportMenuShown = true
  } 

  showDayMenu(){
    this.hideAll()
    this.dayMenuShown = true
  } 

  showBlankMenu(){
    this.hideAll()
  }
  
}
interface IDayData{
  bussinessDate : String
}

interface ICompanyData{
  companyName : String
}

interface ISupplier{
  name : string
}
function finalize(arg0: () => Promise<unknown>): import("rxjs").OperatorFunction<Object, unknown> {
  throw new Error('Function not implemented.');
}


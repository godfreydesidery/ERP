import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AfterContentInit, AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from './../environments/environment';
import { AuthService } from './auth.service';
import { trigger,state,style,animate,transition} from '@angular/animations'; 
import { Observable } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';

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

  constructor(private http  : HttpClient,
    private auth : AuthService,
    private router: Router,
    private spinner: NgxSpinnerService){   
  }
   
  async ngOnInit(): Promise<void> {
    this.ping()
    try{
      await this.loadDay()
    }catch(e:any){}    
    var currentUser = null
    if(localStorage.getItem('current-user') != null){
      currentUser = localStorage.getItem('current-user')
    }
    if(currentUser != null){
      localStorage.setItem('logged-in', 'true')
      await this.router.navigate([''])
    }else{
      localStorage.setItem('logged-in', 'false')
      await this.router.navigate([''])
    }
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
  
}
interface IDayData{
  bussinessDate : String
}

interface ISupplier{
  name : string
}
function finalize(arg0: () => Promise<unknown>): import("rxjs").OperatorFunction<Object, unknown> {
  throw new Error('Function not implemented.');
}


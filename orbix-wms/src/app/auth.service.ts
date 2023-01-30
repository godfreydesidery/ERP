import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Injectable } from '@angular/core'
import * as moment from "moment"
import { BehaviorSubject, Observable } from 'rxjs'
import { IUser } from './IUser'
import { finalize, map } from 'rxjs/operators'
import { JwtHelperService } from '@auth0/angular-jwt'
import { DatePipe } from '@angular/common'
import { environment } from '../environments/environment';
import { NgxSpinnerService } from 'ngx-spinner'

const API_URL = environment.apiUrl;

interface IUserData{
  alias : string
}

interface IDayData{
  bussinessDate : String
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  

  helper = new JwtHelperService()
  

  private currentUserSubject: BehaviorSubject<IUser>
  public currentUser: Observable<IUser>

  public user : {
    username : string, 
    access_token : string, 
    refresh_token : string
  } = JSON.parse(localStorage.getItem('current-user')!) 


  constructor(
    private http : HttpClient,
    private datePipe : DatePipe,
    private spinner : NgxSpinnerService
    ) {
    this.currentUserSubject = new BehaviorSubject<IUser>(JSON.parse(localStorage.getItem('current-user') || '{}'));
    this.currentUser = this.currentUserSubject.asObservable()
  }

  public get currentUserValue(): IUser {
    return this.currentUserSubject.value
  }

  loginUser(passName : string, passCode : string){
    let user = {
      passName : passName,
      passCode : passCode
    }

    this.spinner.show()
    return this.http.post<any>(API_URL+'/wms_pass_in', user)
      .pipe(finalize(() => this.spinner.hide()))
      .pipe(map(user => {
        // store user details and jwt token in local storage to keep user logged in between page refreshes
        localStorage.setItem('current-user', JSON.stringify(user))
        let currentUser : {
          passName      : string, 
          access_token  : string, 
          refresh_token : string,
          salesAgentName : string
        } = JSON.parse(localStorage.getItem('current-user')!)

        let sn : {
          salesAgentName : string
        } = JSON.parse(localStorage.getItem('current-user')!)
        localStorage.setItem('sales-agent-name', sn.salesAgentName)

        if(this.tokenExpired(currentUser.access_token)){
          //should clear user information
          return
        }                
        return user
      }))
  }

  autoLogin(){
    let currentUser : {
      passName      : string, 
      access_token  : string, 
      refresh_token : string
    } = JSON.parse(localStorage.getItem('current-user')!)
    if(!currentUser){
      return
    }

  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('current-user')
    localStorage.removeItem('active-list')
  }

  private tokenExpired(token: string) {
    return this.helper.isTokenExpired(token)
  }



  public async loadUserSession(username : string){

    localStorage.setItem('agent-name', 'VIVA Kinondoni Route(LAYLA)')
      
    let currentUser : {
      username : string, 
      access_token : string, 
      refresh_token : string
    } = JSON.parse(localStorage.getItem('current-user')!) 
    console.log(currentUser)   
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+currentUser.access_token)
    }

    /*await this.http.get<IUserData>(API_URL+'/users/load_user?username='+username, options)
    .toPromise()
    .then(
      data => {
        localStorage.setItem('user-name', data?.alias!+'')  
        localStorage.setItem('username', username)  
      }
    )

    await this.http.get<IDayData>(API_URL+'/days/get_bussiness_date', options)
    .toPromise()
    .then(
      data => {
        localStorage.setItem('system-date', data?.bussinessDate!+'')        
      },
      error => {
        console.log(error)
      }
    )*/

    localStorage.setItem('username', username)  


  }

  public unloadUserSession(){
    localStorage.removeItem('username')
    localStorage.removeItem('user-name')
    localStorage.removeItem('system-date')
  }
}



import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth.service';
import { IShortcut } from 'src/app/models/shortcut';
import {trigger,state,style,animate,transition} from '@angular/animations'; 
import { environment } from 'src/environments/environment';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { NgxSpinnerModule } from "ngx-spinner";
import { Router } from '@angular/router';

const API_URL = environment.apiUrl;


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class DashboardComponent implements OnInit {

  companyName = ''

  public shortcuts: IShortcut[] = [];

  constructor(private auth : AuthService, private http : HttpClient, private shortcut : ShortCutHandlerService, private router : Router) { }

  ngOnInit(): void {
    this.loadShortcuts()
    this.companyName = localStorage.getItem('company-name') + ''
  }

  async loadShortcuts(){
    let options = {
      headers : new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    await this.http.get<IShortcut[]>(API_URL+'/shortcuts/load?username='+localStorage.getItem('username'), options)
     .toPromise()
     .then(
       data => {
         data?.forEach(
           element => {      
             this.shortcuts.push(element)
           }
         )
       }
     )
     .catch(error => {
       console.log(error)
     })
     

  }

  loadMessages(){

  }

  async removeShortcut(shortCutName : string){
    if(confirm('Remove the selected shortcut?')){
      await this.shortcut.removeShortCut(shortCutName)
      await this.router.navigate(['']);
      location.reload()
    }
  }

  removeAllShortcuts(){
    alert('clear all shortcuts!')
  }

}

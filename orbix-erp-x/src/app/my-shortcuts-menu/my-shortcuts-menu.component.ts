import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { AuthService } from '../auth.service';
import { IShortcut } from '../models/shortcut';
import { ShortCutHandlerService } from '../services/short-cut-handler.service';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-my-shortcuts-menu',
  templateUrl: './my-shortcuts-menu.component.html',
  styleUrls: ['./my-shortcuts-menu.component.scss']
})
export class MyShortcutsMenuComponent implements OnInit {

  public shortcuts: IShortcut[] = [];

  constructor(private auth : AuthService, private http : HttpClient, private shortcut : ShortCutHandlerService, private router : Router) { }

  ngOnInit(): void {
    this.loadShortcuts()
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

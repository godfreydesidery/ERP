import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {

  public agentName : string = ''

  public appPages = [
    { title: 'Home', url: '/home', icon: 'home' },
    { title: 'Sale', url: '/sale', icon: 'paper-plane' },
    { title: 'Profile', url: '/profile', icon: 'heart' },
    { title: 'Logout', url: '/logout', icon: 'heart' },
    //{ title: 'Archived', url: '/folder/Archived', icon: 'archive' },
    //{ title: 'Trash', url: '/folder/Trash', icon: 'trash' },
    //{ title: 'Spam', url: '/folder/Spam', icon: 'warning' },
  ];
  public labels = [];
  //public labels = ['Family', 'Friends', 'Notes', 'Work', 'Travel', 'Reminders'];
  constructor(
    private router: Router
  ) {}
  async ngOnInit() {
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
    if(localStorage.getItem('agent-name') != null){
      this.agentName = localStorage.getItem('agent-name')!
    }
    
  }
}

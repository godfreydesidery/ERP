import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {

salesAgentName : string = ''
  public appPages = [
    { title: 'Home', url: '/home', icon: 'home' },
    { title: 'Sale', url: '/sale', icon: 'cash' },
    { title: 'Sales List', url: '/sales-list', icon: 'document-attach' },
    { title: 'Sales Sheet', url: '/sales-sheet', icon: 'document' },
    { title: 'Expense', url: '/sales-expense', icon: 'cash' },
    { title: 'Customer', url: '/customer', icon: 'document' },
    { title: 'Profile', url: '/profile', icon: 'person' },
    { title: 'Logout', url: '/logout', icon: 'exit' },
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
    if(localStorage.getItem('sales-agent-name') == null){
      this.salesAgentName = ''
    }else{
      this.salesAgentName = localStorage.getItem('sales-agent-name')!
    }
    
  }
}

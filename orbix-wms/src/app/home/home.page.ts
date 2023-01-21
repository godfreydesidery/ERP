import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage implements OnInit {

  public activeList : any = ''
  public list: any[] = []
  constructor(private router : Router) { }

  ngOnInit() {
    if(localStorage.getItem('current-user') == null){
      this.router.navigate(['login'], {replaceUrl : true})
    }
    this.showLists()
  }

  showLists(){
    let sn : {
      salesListNo : string[]
    } = JSON.parse(localStorage.getItem('current-user')!)
    this.list = sn.salesListNo 
    
    if(localStorage.getItem('active-list') != null){
      this.activeList = localStorage.getItem('active-list')
    }
  }

  setActiveList(l : string){
    localStorage.setItem('active-list', l)
    this.activeList = l
  }

}

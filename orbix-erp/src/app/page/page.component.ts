import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss']
})
export class PageComponent implements OnInit {

  public userName: string = ''
  public systemDate: string = ''


  isLoggedIn : boolean = false;

  mainMenuShown : boolean = true
  adminMenuShown : boolean = false
  inventoryMenuShown : boolean = false
  mechandizerMenuShown : boolean = false
  productionMenuShown : boolean = false
  supplierRelationsMenuShown : boolean = false
  customerRelationsMenuShown : boolean = false
  humanResourceMenuShown : boolean = false
  reportMenuShown : boolean = false
  dayMenuShown : boolean = false

  constructor(private router : Router) { }

  ngOnInit(): void {

    var currentUser = null
    if(localStorage.getItem('current-user') != null){
      currentUser = localStorage.getItem('current-user')
    }
    if(currentUser != null){
      this.isLoggedIn = true
    }else{
      this.isLoggedIn = false
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
  }

  public async logOut() : Promise<any>{
    localStorage.removeItem('current-user')
    alert('You have logged out!')
    await this.router.navigate([''])
    window.location.reload()
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

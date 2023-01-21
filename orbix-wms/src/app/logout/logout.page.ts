import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.page.html',
  styleUrls: ['./logout.page.scss'],
})
export class LogoutPage implements OnInit {

  constructor(private router : Router,
    private toastController: ToastController) { }

  async ngOnInit() {
    localStorage.setItem('logged-in', 'false')
    localStorage.removeItem('current-user')
    this.clearLists()
    if(localStorage.getItem('logged-in') == 'false'){
      const toast = await this.toastController.create({
        message: 'You are logged out!',
        duration: 1500,
        position: 'top'
      });
      await toast.present()
      this.router.navigate(['login'], {replaceUrl : true})
    }    
  }

  clearLists(){
    localStorage.removeItem('active-list')
  }
}

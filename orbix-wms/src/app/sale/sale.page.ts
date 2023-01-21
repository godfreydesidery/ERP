import { Component, ComponentRef, HostListener, OnDestroy, OnInit } from '@angular/core';

import { ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { IonModal } from '@ionic/angular';
import { OverlayEventDetail } from '@ionic/core/components';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-sale',
  templateUrl: './sale.page.html',
  styleUrls: ['./sale.page.scss'],
})
export class SalePage implements OnInit {

  @ViewChild(IonModal)
  modal!: IonModal;
  message = '';
  name: string = '';
  constructor(private router : Router,
    private toastController: ToastController) { }

  ngOnInit() {
   this.loadSalesList()
  }

  cancel() {
    this.modal.dismiss(null, 'cancel');
  }

  confirm() {
    this.modal.dismiss(this.name, 'confirm');
  }

  onWillDismiss(event: Event) {
    const ev = event as CustomEvent<OverlayEventDetail<string>>;
    if (ev.detail.role === 'confirm') {
      this.message = `Confirmed, ${ev.detail.data}!`;
    }
  }

  async loadSalesList(){
    if(localStorage.getItem('active-list') == null){
      this.router.navigate([''], {replaceUrl : true})
      const toast = await this.toastController.create({
        message: 'Could not load sales list. Please activate a sales list to proceed!',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
    }else{
      /**
       * Load sales list and customers data
       */
      /**
       * Loading sales list data
       */
      const toast = await this.toastController.create({
        message: 'Loading sales list ' +localStorage.getItem('active-list') +' Please wait...',
        duration: 2000,
        position: 'top'
      });
      await toast.present();

    }
  }

}

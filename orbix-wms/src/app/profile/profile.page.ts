import { Component, ComponentRef, HostListener, OnDestroy, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { IonModal, PopoverController } from '@ionic/angular';
import { OverlayEventDetail } from '@ionic/core/components';
import { ToastController } from '@ionic/angular';

import { environment } from '../../environments/environment';
import { AuthService } from '../auth.service';

import { NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';

import { ModalController, NavParams } from '@ionic/angular';

import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';

const API_URL = environment.apiUrl;


@Component({
  selector: 'app-profile',
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.scss'],
})
export class ProfilePage implements OnInit {

  name : string = ''
  contactName : string = ''
  mobile : string = ''
  email : string = ''
  salesTarget : number = 0


  constructor(private router : Router,
    public modalCtrl: ModalController,
    private modalService: NgbModal,
    public popoverController: PopoverController,
    private toastController: ToastController,
    private http : HttpClient) { }



  ngOnInit() {
   this.getProfile()
  }


  async getProfile(): Promise<void> {
    let options = {
      //headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    
    await this.http.get<SalesAgentProfile>(API_URL+'/wms_get_profile?sales_agent_name='+localStorage.getItem('sales-agent-name'), options)
    .toPromise()
    .then(
      data => {
        this.name = data!.name
        this.contactName = data!.contactName
        this.mobile = data!.mobile
        this.email = data!.email
        this.salesTarget = data!.salesTarget

        
      }
    )
    .catch(
      error => {
        console.log(error)
        alert('could not load profile')
        this.router.navigate(['home'])
        location.reload()
      }
    )
  }

}

export interface SalesAgentProfile{
  id : any
  name : string
  contactName : string
  mobile : string
  email: string
  salesTarget : number
}

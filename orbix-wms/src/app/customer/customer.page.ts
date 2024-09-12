import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { IonModal, ModalController, PopoverController, ToastController } from '@ionic/angular';
import { OverlayEventDetail } from '@ionic/core/components';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl;


@Component({
  selector: 'app-customer',
  templateUrl: './customer.page.html',
  styleUrls: ['./customer.page.scss'],
})
export class CustomerPage implements OnInit {
  salesAgentCustomer: ISalesAgentCustomerModel | undefined 

  salesAgentCustomers : ISalesAgentCustomerModel[] = []

  id : any
  name : string = ''
  location : string = ''
  mobile : string = ''

  message : string = ''

  recordMode : string = 'add'

  @ViewChild(IonModal)
  modal!: IonModal;

  constructor(private router : Router,
    public modalCtrl: ModalController,
    private modalService: NgbModal,
    public popoverController: PopoverController,
    private toastController: ToastController,
    private http : HttpClient,
    private spinner : NgxSpinnerService) { }

  ngOnInit() {
    this.getSalesAgentCustomers()
  }

  clearValues(){
    this.id  = null
    this.name = ''
    this.location  = ''
    this.mobile = ''
  }

  clear(){
    this.id  = null
    this.name = ''
    this.location  = ''
    this.mobile = ''
  }


  async getSalesAgentCustomers(): Promise<void> {
    this.salesAgentCustomers = []
    this.clearValues()
    let options = {
      //headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    
    
    this.spinner.show()
    await this.http.get<ISalesAgentCustomerModel[]>(API_URL+'/wms_load_sales_agent_customers?sales_agent_name='+localStorage.getItem('sales-agent-name'), options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        let customers : ISalesAgentCustomerModel[] = data!
        customers.forEach(element => {
          this.salesAgentCustomers.push(element)
        })
      }
    )
    .catch(
      error => {
        console.log(error)
        alert('could not load  customers')
        this.router.navigate(['home'])
        location.reload()
      }
    )
  }

  async addCustomer(){
    if(this.name == '' || this.location == ''){
      alert('Name and location required')
      return
    }
    if(window.confirm('Add customer?')){
      //confirm 
    }else{
      //do  not confirm
      return
    }
    /**
     * Confirmation logic here
     */
    var salesAgentName = localStorage.getItem('sales-agent-name')!.toString()
    var customer : ISalesAgentCustomerModel = {
      id : null,
      name : this.name,
      location : this.location,
      mobile : this.mobile,
      salesAgent : {
        name : salesAgentName
      }
    }

    if(salesAgentName == null){
      alert('Could not perform operation, agent name not available')
      return
    }

    this.spinner.show()
    await this.http.post(API_URL+'/sales_agent_customer/save', customer)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        //this.router.navigate(['home'])
        alert('Customer added')
        //this.router.navigate(['sales-expense'])
        this.getSalesAgentCustomers()
      }
    )
    .catch(
      async error => {
        console.log(error)
        const toast = await this.toastController.create({
          message: 'Could not add customer',
          duration: 2000,
          position: 'top'
        });
        await toast.present();
      }
    )

    
  }

  async delete(id : any){


    var present : boolean = false

    var salesAgentName = localStorage.getItem('sales-agent-name')!.toString()
    
    var customer : ISalesAgentCustomerModel = {
      id: id,
      name : '',
      location : '',
      mobile : '',
      salesAgent : {
        name : salesAgentName
      }
    }

    this.spinner.show()
    await this.http.post(API_URL+'/sales_agent_customer/delete', customer)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        //this.router.navigate(['home'])
        alert('Customer deleted')
        //this.router.navigate(['sales-expense'])
        this.getSalesAgentCustomers()
      }
    )
    .catch(
      async error => {
        console.log(error)
        const toast = await this.toastController.create({
          message: 'Could not delete customer',
          duration: 2000,
          position: 'top'
        });
        await toast.present();
      }
    )
    
    
  }

  cancel() {
    this.modal.dismiss(null, 'cancel')
  }

  confirm() {
    this.modal.dismiss('', 'confirm')
  }

  onWillDismiss(event: Event) {
    const ev = event as CustomEvent<OverlayEventDetail<string>>;
    if (ev.detail.role === 'confirm') {
      this.message = `Confirmed, ${ev.detail.data}!`;
    }
  }
}

export interface ISalesAgentCustomerModel {
  id             : any
  name           : string
  location       : string
  mobile         : string
  salesAgent : ISalesAgent
}

export interface ISalesAgent {
  name : string
}
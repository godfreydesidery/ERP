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
  selector: 'app-sales-expense',
  templateUrl: './sales-expense.page.html',
  styleUrls: ['./sales-expense.page.scss'],
})
export class SalesExpensePage implements OnInit {

  salesExpense: SalesExpenseModel | undefined 

  salesExpenses : SalesExpenseModel[] = []

  totalAmount : number = 0

  description : string = ''
  amount : number = 0

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
    this.getSalesExpenses()
  }

  clearValues(){

    this.totalAmount  = 0
    this.amount = 0

    this.description = ''
  }

  clear(){
    this.description = ''
    this.amount = 0
  }


  async getSalesExpenses(): Promise<void> {
    this.salesExpenses = []
    this.clearValues()
    let options = {
      //headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    
    if(localStorage.getItem('active-list') == null){
      const toast = await this.toastController.create({
        message: 'No active sales list selected',
        duration: 2000,
        position: 'top'
      });
      await toast.present();
      this.router.navigate(['home'])
      location.reload()
    }
    this.spinner.show()
    await this.http.get<SalesExpenseModel[]>(API_URL+'/wms_load_sales_expenses?sales_list_no='+localStorage.getItem('active-list'), options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        let expenses : SalesExpenseModel[] = data!
        expenses.forEach(element => {
          this.salesExpenses.push(element)
          this.totalAmount  = this.totalAmount + (element.amount)
        })
      }
    )
    .catch(
      error => {
        console.log(error)
        alert('could not load  sales list')
        this.router.navigate(['home'])
        location.reload()
      }
    )
  }

  async addExpense(){
    if(window.confirm('Add this expense?')){
      //confirm 
    }else{
      //do  not confirm
      return
    }
    /**
     * Confirmation logic here
     */
    var salesListNo = localStorage.getItem('active-list')!.toString()
    var expense : SalesExpenseModel = {
      id : null,
      description : this.description,
      amount : this.amount,
      salesListNo : salesListNo
      
    }

    if(salesListNo == null){
      alert('Could not perform operation, sales list not available')
      return
    }

    this.spinner.show()
    await this.http.post(API_URL+'/wms_expense/save', expense)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        //this.router.navigate(['home'])
        alert('Expense added')
        //this.router.navigate(['sales-expense'])
        this.getSalesExpenses()
      }
    )
    .catch(
      async error => {
        console.log(error)
        const toast = await this.toastController.create({
          message: 'Could not add expense',
          duration: 2000,
          position: 'top'
        });
        await toast.present();
      }
    )

    
  }

  async delete(id : any){


    var present : boolean = false

    var salesListNo = localStorage.getItem('active-list')!.toString()
    
    var expense : SalesExpenseModel = {
      id: id,
      description : '',
      amount : 0,
      salesListNo : salesListNo
    }

    this.spinner.show()
    await this.http.post(API_URL+'/wms_expense/delete', expense)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        //this.router.navigate(['home'])
        alert('Expense deleted')
        //this.router.navigate(['sales-expense'])
        this.getSalesExpenses()
      }
    )
    .catch(
      async error => {
        console.log(error)
        const toast = await this.toastController.create({
          message: 'Could not delete expense',
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

export interface SalesExpenseModel{
  id : any
  description : string
  amount : number
  salesListNo      : string
}


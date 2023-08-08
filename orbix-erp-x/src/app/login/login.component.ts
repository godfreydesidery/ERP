import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, Router } from '@angular/router';
import { delay } from 'rxjs';
import { first } from 'rxjs/internal/operators/first';
import { AuthService } from '../auth.service';
import { ErrorHandlerService } from '../services/error-handler.service';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  /**Login credentials */
  username  : string
  password  : string

  /**Checks the login status */
  status : string = ''

  /**User alert message */
  message : any = ''

  /**For modal display */
  closeResult : string = ''

  constructor(
    private http :HttpClient,
    private auth : AuthService, 
    private modalService: NgbModal,
    private router: Router, 
    private authService: AuthService) {  
    
    this.username = ''
    this.password = ''  

  }

  ngOnInit(): void {
    this.status   = ''
    this.message  = ''
    this.username = ''
    this.password = '' 
  }

  async loginUser(){
    localStorage.removeItem('user-name')
    localStorage.removeItem('system-date')

    if(this.username == '' || this.password == ''){ 
      alert('Please fill in your username and password')
      return
    }
    this.status = 'Loading... Please wait.'
    await this.auth.loginUser(this.username, this.password)
      .pipe(first())
      .toPromise()
      .then(
        async data => {
          this.status = 'Loading User... Please wait.'
          await this.auth.loadUserSession(this.username)
          this.status = 'Authenticated'
          window.location.reload()
        }
      )
      .catch(error => {
        this.status = ''
        localStorage.removeItem('current-user')
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Invalid username and password')
        return
      })    
  }
  
  /**Clear credential fields to allow new entry */
  clearFields(){
    this.username = ''
    this.password = ''
  }

  /** */
  contactAdministrator(){
    alert('Please contact System Administrator for password recovery')
  }

  clearCredentials(event : any){
    event.target.value = ''
  }


  /**This is a pop up intended to display messages to the user. It has been used as a pilot. To be implemented to other components */
  showMessage(message: any) {
    this.modalService.open(message, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    this.message = ''
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
}

/**A user model */
export interface User{
  firstName   : string
  secondName  : string
  lastName    : string
}
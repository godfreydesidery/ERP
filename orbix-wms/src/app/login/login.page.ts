import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import { first } from 'rxjs';

import { environment } from '../../environments/environment';
import { AuthService } from '../auth.service';

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  /**Login credentials */
  passName  : string
  passCode  : string

  /**Checks the login status */
  status : string = ''

  /**User alert message */
  message : any = ''

  /**For modal display */
  closeResult : string = ''

  constructor(
              private auth : AuthService, 
              private router: Router, 
              private authService: AuthService,
              private toastController: ToastController) {
      this.passName = ''
      this.passCode = ''  
    }

  ngOnInit() {
    /**
     * Remove current user data if present
     * to allow reloading
     */
    localStorage.removeItem('current-user')
  }
    
  async login() {
    if(this.passName == '' || this.passCode == ''){ 
      const toast = await this.toastController.create({
        message: 'Please fill in passname and passcode!',
        duration: 1500,
        position: 'top'
      });
      await toast.present();
      return
    }
    this.status = 'Loading... Please wait.'
    await this.auth.loginUser(this.passName, this.passCode)
      .pipe(first())
      .toPromise()
      .then(
        async () => {
          this.status = 'Loading User... Please wait.'
          await this.auth.loadUserSession(this.passName)          
          localStorage.setItem('logged-in', 'true')
          this.status = 'Authenticated'
          this.router.navigate([''])
          location.reload()
          
          
        }
      )
      .catch(async error => {
        console.log(error)
        localStorage.setItem('logged-in', 'false')
        this.status = ''
        localStorage.removeItem('current-user')
        const toast = await this.toastController.create({
          message: 'Could not log in, invalid passname and passcode!',
          duration: 1500,
          position: 'top'
        })
        await toast.present();
        this.router.navigate(['login'])
        return
      })  
  }

  reset(){
    this.passName = ''
    this.passCode = ''
  }
}

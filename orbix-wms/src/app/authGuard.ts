import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';


//const API_URL = environment.apiUrl;


@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  helper = new JwtHelperService()

    constructor(
        private router: Router,
    ) { }

    canActivate(route: ActivatedRouteSnapshot): boolean {
        let authInfo = localStorage.getItem('logged-in')
        if(authInfo == null){
            localStorage.setItem('logged-in', 'false')
            authInfo = localStorage.getItem('logged-in')
        }

        if (authInfo == 'false') {
          this.router.navigate(['login']);
          return false;
        }
        return true;
      }

    private tokenExpired(token: string) {
      return this.helper.isTokenExpired(token)
    }
}
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthenticationService} from "../service/authentication.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url.includes(`${this.authenticationService.host}/login`)){
      return next.handle(request);
    }

    // console.log(`${this.authenticationService.host}/users`)
    // console.log(request);
    // console.log(request.method);
    // console.log(request.url.includes(`${this.authenticationService.host}/users`));
    // console.log(request.method == 'post');
    if (request.url.includes(`${this.authenticationService.host}/users`)  && !request.url.endsWith('e') && request.method === 'POST'){
      // console.log('TRRUEE')
      return next.handle(request);
    }

    const token = this.authenticationService.getToken();
    const requestWithToken = request.clone({setHeaders: {Authentication: `Bearer: ${token}`}});

    return next.handle(requestWithToken);
  }
}

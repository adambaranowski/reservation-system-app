import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthenticationService} from "../service/authentication.service";
import {environment} from "../../environments/environment";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authenticationService: AuthenticationService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url.includes(`${environment.authApiUrl}/login`)){
      return next.handle(request);
    }

    if (request.url.includes(`${environment.apiUrl}/users`)  && !request.url.endsWith('e') && request.method === 'POST'){
      return next.handle(request);
    }

    const token = this.authenticationService.getToken();
    const requestWithToken = request.clone({setHeaders: {Authentication: `Bearer: ${token}`}});

    return next.handle(requestWithToken);
  }
}

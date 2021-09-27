import { Injectable } from '@angular/core';
import { environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {UserLoginDto} from "../model/UserLoginDto";
import {User} from "../model/User";
import {JwtHelperService} from "@auth0/angular-jwt";
import {NotificationType} from "../enum/notification-type";
import {Urls} from "../enum/urls";
import {UserLoginResponseDto} from "../model/UserLoginResponseDto";
import {UserService} from "./user.service";
import {NotificationService} from "./notification.service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  public host: string = environment.apiUrl;

  private token: string | undefined;
  private user: User | undefined;

  private jwtHelper = new JwtHelperService();

  constructor(private httpClient: HttpClient,
              private userService: UserService,
              private notifier: NotificationService) { }

  public sendLoginRequest(loginDto: UserLoginDto): Observable<UserLoginResponseDto> {
    return this.httpClient
      .post<UserLoginResponseDto>
      (`${this.host}/${Urls.LOGIN}`, loginDto);
  }


  public logOut(): void {
    this.token = undefined;
    this.user = undefined;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  public saveUserdata(loginResponseDto: UserLoginResponseDto): void {
    console.log('dto token:' + loginResponseDto.token)
    this.token = loginResponseDto.token;
    localStorage.setItem('token', loginResponseDto.token);

    this.userService.getUserById(loginResponseDto.userId).subscribe(
      (response: User) => {
        this.user = response;
        localStorage.setItem('user', JSON.stringify(response));
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.sendNotification(NotificationType.ERROR, 'Login failure! Cannot load user data ' + httpErrorResponse.error);
        this.token = undefined;
        this.user = undefined;
      }
    )

  }

  private sendNotification(type: NotificationType, message: any): void {
    if (message) {
      this.notifier.showNotification(type, message);
    } else {
      this.notifier.showNotification(type, 'Logging error')
    }

  }

  public getUser(): User | undefined {
    this.loadDataFromStorage();
    return this.user;
  }

  public getAuthorities(): string[] | undefined {
    this.loadDataFromStorage();
    return this.user?.authorities;
  }

  public loadDataFromStorage(): void {
    this.token = <string>localStorage.getItem('token');
    this.user = JSON.parse(<string>localStorage.getItem('user'));
  }

  public getToken(): string | undefined {
    this.loadDataFromStorage();
    return this.token;
  }

  public isLoggedIn(): boolean {
    this.loadDataFromStorage();
    if (this.token != undefined && this.token !== ''){
        if(!this.jwtHelper.isTokenExpired(this.token)){
          return true;
        }
    } else {
      this.logOut();
      return false;
    }
    return false;
  }
}

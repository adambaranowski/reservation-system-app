import {Injectable, OnDestroy} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, Subject, Subscription} from "rxjs";
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
export class AuthenticationService implements OnDestroy {
  private subscriptions: Subscription[] = [];

  private token: string | undefined;
  private user: User | undefined;

  private jwtHelper = new JwtHelperService();

  constructor(private httpClient: HttpClient,
              private userService: UserService,
              private notifier: NotificationService) {
  }

  // public sendLoginRequest(loginDto: UserLoginDto): Observable<UserLoginResponseDto> {
  //   return this.httpClient
  //     .post<UserLoginResponseDto>
  //     (`${environment.authApiUrl}/${Urls.LOGIN}`, loginDto);
  // }

  public login(loginDto: UserLoginDto): Observable<boolean> {

    var isLoggedSubject: Subject<boolean> = new Subject<boolean>();

    this.httpClient.post<UserLoginResponseDto>(`${environment.authApiUrl}/${Urls.LOGIN}`, loginDto).subscribe(
      (response: UserLoginResponseDto) => {

        // load token to allow interceptor o use it during next api call
        this.saveToken(response.token);

        this.httpClient.get<User>(`${environment.apiUrl}/${Urls.LOGIN}`).subscribe(
          (user: User) => {

            this.saveUser(user);
            this.sendNotification(NotificationType.SUCCESS, 'Successfully logged in!');

            isLoggedSubject.next(true);
          },
          (httpErrorResponse: HttpErrorResponse) => {
            console.log(httpErrorResponse);
            this.sendNotification(NotificationType.ERROR, 'Login failure! ' + httpErrorResponse.error);

            isLoggedSubject.next(false);
          }
        );
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.sendNotification(NotificationType.ERROR, 'Login failure! ' + httpErrorResponse.error);

        isLoggedSubject.next(false);
      }
    );
    return isLoggedSubject.asObservable();
  }


  public logOut(): void {
    this.token = undefined;
    this.user = undefined;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  public saveUser(user: User): void {
    this.user = user;
    localStorage.setItem('user', JSON.stringify(user));
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
    if (this.token != undefined && this.token !== '') {
      if (!this.jwtHelper.isTokenExpired(this.token)) {
        return true;
      }
    } else {
      this.logOut();
      return false;
    }
    return false;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe())
  }
}

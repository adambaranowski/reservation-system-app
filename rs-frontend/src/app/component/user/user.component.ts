import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {UserService} from "../../service/user.service";
import {Subscription} from "rxjs";
import {NotificationService} from "../../service/notification.service";
import {NotificationType} from "../../enum/notification-type";
import {HttpErrorResponse} from "@angular/common/http";
import {User} from "../../model/User";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy{
  private subscriptions: Subscription[] = [];
  public users: User[] = [];

  public displayEditForm = false;

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private userService: UserService,
              private notifier: NotificationService) { }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.reloadUsers();
    } else {
      this.router.navigateByUrl('/login')
    }
  }

  public reloadUsers(): void {
    this.subscriptions.push(this.userService.getUsers().subscribe(
      (response: User[] ) => {
        this.users = response;

      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR, 'Cannot get Users' + httpErrorResponse.error);
      }
    ))
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  public lockedAccountText(user: User): string {
    if(user.accountNonLocked) {
      return "ACTIVE";
    } else {
      return "LOCKED";
    }
  }

  enableUser(user: User): void {
    this.subscriptions.push(this.userService.enableUsers([user.id]).subscribe(
      (response: any ) => {
        this.notifier.showNotification(NotificationType.INFO, 'User enabled');
        this.reloadUsers();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR,  httpErrorResponse.error);
      }
    ))
}

  disableUser(user: User): void {
    this.subscriptions.push(this.userService.disableUsers([user.id]).subscribe(
      (response: any ) => {
        this.notifier.showNotification(NotificationType.WARNING, 'User disabled');
        this.reloadUsers();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR,  httpErrorResponse.error);
      }
    ))
  }

  deleteUser(user: User): void {
    this.subscriptions.push(this.userService.deleteUser(user.id).subscribe(
      (response: any ) => {
        this.notifier.showNotification(NotificationType.WARNING, 'User deleted');
        this.reloadUsers();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR,  httpErrorResponse.error);
      }
    ))
  }

}

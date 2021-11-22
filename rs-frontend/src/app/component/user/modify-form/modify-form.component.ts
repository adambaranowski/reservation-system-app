import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../service/authentication.service";
import {UserService} from "../../../service/user.service";
import {NotificationService} from "../../../service/notification.service";
import {User} from "../../../model/User";
import {NotificationType} from "../../../enum/notification-type";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-modify-form',
  templateUrl: './modify-form.component.html',
  styleUrls: ['./modify-form.component.css']
})
export class ModifyFormComponent implements OnInit, OnDestroy {
  @Input()
  userId = 0;

  @Input()
  display = false;

  // userNick = new FormControl();
  // email = new FormControl();
  // password = new FormControl();

  isStudent = false;
  isTeacher = false;
  isAdmin = false;

  private subscriptions: Subscription[] = [];

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private userService: UserService,
              private notifier: NotificationService) {
  }

  ngOnInit(): void {

  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }


  public addAuthorities(): void {
    const authorities = [];

    if (this.isAdmin) {
      authorities.push('ADMIN');
    }

    if (this.isTeacher) {
      authorities.push('TEACHER');
    }

    if (this.isStudent) {
      authorities.push('STUDENT');
    }

    //Add default student
    if (authorities.length < 1) {
      authorities.push('STUDENT');
    }

    this.subscriptions.push(
      this.userService.modifyAuthorities(this.userId, authorities).subscribe(
        (response: User) => {

          this.sendNotification(NotificationType.INFO, 'Successfully modified!');
          window.location.reload();
        },
        (httpErrorResponse: HttpErrorResponse) => {
          console.log(httpErrorResponse);
          this.sendNotification(NotificationType.ERROR, 'Failure! ' + httpErrorResponse.error);
        }
      )
    );
  }


  private sendNotification(type: NotificationType, message: any): void {
    if (message) {
      this.notifier.showNotification(type, message);
    } else {
      this.notifier.showNotification(type, 'Logging error')
    }

  }

}

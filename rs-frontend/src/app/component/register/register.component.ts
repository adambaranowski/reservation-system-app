import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {NotificationService} from "../../service/notification.service";
import {NotificationType} from "../../enum/notification-type";
import {FormControl} from "@angular/forms";
import {UserRequestDto} from "../../model/UserRequestDto";
import {UserService} from "../../service/user.service";
import {User} from "../../model/User";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  userNick = new FormControl();
  email = new FormControl();
  password = new FormControl();

  isStudent = new FormControl();
  isTeacher = new FormControl();
  isAdmin = new FormControl();

  private subscriptions: Subscription[] = [];

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private userService: UserService,
              private notifier: NotificationService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/menu')
    } else {
      this.router.navigateByUrl('/register')
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public backToLogin(): void {
    this.router.navigateByUrl('login');
  }

  public onRegister(): void {
    const authorities: [string | null] = ['STUDENT'];

    if(this.isAdmin.pristine){
      authorities.push('ADMIN');
    }

    if(this.isTeacher.pristine){
      authorities.push('STUDENT');
    }

    if(this.isStudent.pristine){
      authorities.push('TEACHER');
    }

    //Add default student
    if (authorities.length < 1) {
        authorities.push('STUDENT');
    }

    const registerDto: UserRequestDto = {
      email: this.email.value,
      userNick: this.userNick.value,
      password: this.password.value,
      authorities: authorities
    };

    console.log(registerDto);

    this.subscriptions.push(
      this.userService.register(registerDto).subscribe(
        (response: User ) => {
          console.log(response);

          this.sendNotification(NotificationType.SUCCESS, 'Successfully registred!');

          setTimeout(() => { this.router.navigateByUrl('/login'); }, 1000);

        },
        (httpErrorResponse: HttpErrorResponse) => {
          console.log(httpErrorResponse);
          this.sendNotification(NotificationType.ERROR, 'Register failure! ' + httpErrorResponse.error);
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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {NotificationService} from "../../service/notification.service";
import {Subscription} from "rxjs";
import {UserLoginDto} from "../../model/UserLoginDto";
import {NotificationType} from "../../enum/notification-type";
import {FormControl} from "@angular/forms";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  email = new FormControl();
  password = new FormControl();

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private notifier: NotificationService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/menu')
    } else {
      this.router.navigateByUrl('/login')
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onLogin(): void {
    const loginDto: UserLoginDto = {
      email: this.email.value,
      password: this.password.value
    }

    this.subscriptions.push(
      this.authenticationService.login(loginDto).subscribe(
        (isLoggedSuccessfully: boolean) => {

          if (isLoggedSuccessfully) {
            setTimeout(() => {
              this.router.navigateByUrl('/menu');
            }, 1000);
          }
        }
      )
    );
  }

  public onRegister(): void {
    this.router.navigateByUrl('/register');
  }

  private sendNotification(type: NotificationType, message: any): void {
    if (message) {
      this.notifier.showNotification(type, message);
    } else {
      this.notifier.showNotification(type, 'Logging error')
    }

  }
}

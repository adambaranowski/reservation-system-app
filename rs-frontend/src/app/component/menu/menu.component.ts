import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../../service/authentication.service";
import {Router} from "@angular/router";
import {UserService} from "../../service/user.service";
import {NotificationService} from "../../service/notification.service";
import {BehaviorSubject} from "rxjs";
import {NotificationType} from "../../enum/notification-type";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  public userNick: string | null = '';
  private titleSubject = new BehaviorSubject<string>('Menu');
  public titleAction$ = this.titleSubject.asObservable();
  public isAdmin: boolean = false

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private userService: UserService,
              private notifier: NotificationService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      //this.router.navigateByUrl('/menu')
    } else {
      this.router.navigateByUrl('/login')
    }

    let authorities = this.authenticationService.getAuthorities();
    if (authorities) {
      if (authorities.indexOf('ADMIN') > -1) {
        this.isAdmin = true;
      }
    }

    let userNick = this.authenticationService.getUser()?.userNick;

    //console.log(this.authenticationService.getUser());

    if (userNick)
      this.userNick = userNick;
  }

  changeTitle(newTitle: string): void {
    this.titleSubject.next(newTitle);
  }

  logout(): void {
    this.authenticationService.logOut();
    this.notifier.showNotification(NotificationType.INFO, 'Logging out');
    this.router.navigateByUrl('/login');
  }

}

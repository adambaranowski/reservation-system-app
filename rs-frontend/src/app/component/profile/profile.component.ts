import { Component, OnInit } from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {UserService} from "../../service/user.service";
import {NotificationService} from "../../service/notification.service";
import {User} from "../../model/User";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User | undefined;

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private userService: UserService,
              private notifier: NotificationService ) { }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      //this.router.navigateByUrl('/menu')
    } else {
      this.router.navigateByUrl('/login')
    }
    this.user = this.authenticationService.getUser();
  }


}

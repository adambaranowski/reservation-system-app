import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {User} from "../../model/User";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User | undefined;

  constructor(private router: Router,
              private authenticationService: AuthenticationService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
    } else {
      this.router.navigateByUrl('/login')
    }
    this.user = this.authenticationService.getUser();
  }


}

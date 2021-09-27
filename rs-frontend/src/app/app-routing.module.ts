import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoginComponent} from "./component/login/login.component";
import {RegisterComponent} from "./component/register/register.component";
import {MenuComponent} from "./component/menu/menu.component";
import {AppComponent} from "./app.component";
import {RoomComponent} from "./component/room/room.component";
import {ReservationComponent} from "./component/reservation/reservation.component";
import {UserComponent} from "./component/user/user.component";
import {ProfileComponent} from "./component/profile/profile.component";

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'menu', component: MenuComponent, children: [
    {path: 'rooms', component: RoomComponent},
      {path: 'reservations', component: ReservationComponent},
      {path: 'users', component: UserComponent},
      {path: 'profile', component: ProfileComponent},]
},
  {path: '', redirectTo: '/login', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

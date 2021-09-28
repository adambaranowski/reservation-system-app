import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {AuthInterceptor} from "./interceptor/auth.interceptor";
import {AuthenticationGuard} from "./guard/authentication.guard";
import {NotificationModule} from "./notification.module";
import { LoginComponent } from './component/login/login.component';
import { MenuComponent } from './component/menu/menu.component';
import { RegisterComponent } from './component/register/register.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { UserComponent } from './component/user/user.component';
import { RoomComponent } from './component/room/room.component';
import { ReservationComponent } from './component/reservation/reservation.component';
import { EquipmentComponent } from './component/equipment/equipment.component';
import { ProfileComponent } from './component/profile/profile.component';
import {ModifyFormComponent} from "./component/user/modify-form/modify-form.component";
import { EquipmentFormComponent } from './component/equipment/equipment-form/equipment-form.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        MenuComponent,
        RegisterComponent,
        UserComponent,
        RoomComponent,
        ReservationComponent,
        EquipmentComponent,
        ProfileComponent,
        ModifyFormComponent,
        ModifyFormComponent,
        EquipmentFormComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        NotificationModule,
        FormsModule,
        ReactiveFormsModule,
    ],
  providers: [AuthenticationGuard, {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }

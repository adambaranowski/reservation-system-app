import { Injectable } from '@angular/core';
import {NotificationModule} from "../notification.module";
import {NotifierService} from "angular-notifier";
import {NotificationType} from "../enum/notification-type";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private notifier: NotifierService) { }

  public showNotification(type: NotificationType, message: string) {
    this.notifier.notify(type, message);
  }
}

import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {RoomResponseDto} from "../../../../model/roomResponseDto";
import {FormControl} from "@angular/forms";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../../service/authentication.service";
import {RoomEquipmentService} from "../../../../service/room-equipment.service";
import {NotificationService} from "../../../../service/notification.service";
import {Subscription} from "rxjs";
import {NotificationType} from "../../../../enum/notification-type";
import {HttpErrorResponse} from "@angular/common/http";
import {RoomRequestDto} from "../../../../model/roomRequestDto";
import RoomStatusEnum = RoomRequestDto.RoomStatusEnum;

@Component({
  selector: 'app-add-form',
  templateUrl: './add-form.component.html',
  styleUrls: ['./add-form.component.css']
})
export class AddFormComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  @Input()
  display: boolean = false;

  roomNumber = new FormControl();
  description = new FormControl();
  roomStatus = new FormControl();

  statusValues = Object.values(RoomStatusEnum);

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private roomEquipmentService: RoomEquipmentService,
              private notifier: NotificationService) {
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  ngOnInit(): void {
  }

  performSubmit(): void {

    let status;
    switch (this.roomStatus.value) {
      case RoomStatusEnum.NORMAL.valueOf():
        status = RoomStatusEnum.NORMAL;
        break;
      case RoomStatusEnum.ONLYPRINCIPAL.valueOf():
        status = RoomStatusEnum.ONLYPRINCIPAL;
        break;
      case RoomStatusEnum.ONLYTEACHER.valueOf():
        status = RoomStatusEnum.ONLYTEACHER;
        break;

    }

    const newRoom: RoomRequestDto = {
      description: this.description.value,
      roomNumber: this.roomNumber.value,
      roomStatus: status,
      equipmentItemsId: []
    }

    this.subscriptions.push(
      this.roomEquipmentService.addRoom(newRoom).subscribe(
        (response: RoomResponseDto) => {
          this.sendNotification(NotificationType.INFO, "Room added!");
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

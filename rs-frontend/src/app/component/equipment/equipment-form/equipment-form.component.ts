import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Subscription} from "rxjs";
import {NotificationService} from "../../../service/notification.service";
import {NotificationType} from "../../../enum/notification-type";
import {HttpErrorResponse} from "@angular/common/http";
import {RoomEquipmentService} from "../../../service/room-equipment.service";
import {EquipmentRequestDto} from "../../../model/equipmentRequestDto";
import {EquipmentResponseDto} from "../../../model/equipmentResponseDto";

@Component({
  selector: 'app-equipment-form',
  templateUrl: './equipment-form.component.html',
  styleUrls: ['./equipment-form.component.css']
})
export class EquipmentFormComponent implements OnInit, OnDestroy {

  @Input()
  option = 'ADD';

  @Input()
  display = false;

  @Input()
  roomNumbersList: any[] = [];

  @Input()
  equipmentEditId: number | undefined = -1;

  name = new FormControl();
  description = new FormControl();
  roomNumber = new FormControl();

  private subscriptions: Subscription[] = [];

  constructor(private equipmentService: RoomEquipmentService, private notifier: NotificationService) {
  }

  ngOnInit(): void {

  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public performSubmit(): void {
    if (this.option === 'ADD') {
      this.addEquipment()
    }

    if (this.option === 'EDIT') {
      this.editEquipment();
    }
  }

  public addEquipment(): void {

    const equipmentRequestDto: EquipmentRequestDto = {
      name: this.name.value,
      description: this.description.value,
      roomNumber: this.roomNumber.value,
    };

    this.subscriptions.push(
      this.equipmentService.addEquipment(equipmentRequestDto).subscribe(
        (response: EquipmentResponseDto) => {

          this.sendNotification(NotificationType.SUCCESS, 'Equipment added!');

        },
        (httpErrorResponse: HttpErrorResponse) => {
          console.log(httpErrorResponse);
          this.sendNotification(NotificationType.ERROR, 'Failure! ' + httpErrorResponse.error);
        }
      )
    );
  }

  public editEquipment(): void {

    const equipmentRequestDto: EquipmentRequestDto = {
      name: this.name.value,
      description: this.description.value,
      roomNumber: this.roomNumber.value,
    };

    if (this.equipmentEditId)
      this.subscriptions.push(
        this.equipmentService.modifyEquipment(equipmentRequestDto, this.equipmentEditId).subscribe(
          (response: EquipmentResponseDto) => {

            this.sendNotification(NotificationType.INFO, 'Equipment Edited!');

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

import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Subscription} from "rxjs";
import {NotificationService} from "../../../service/notification.service";
import {NotificationType} from "../../../enum/notification-type";
import {HttpErrorResponse} from "@angular/common/http";
import {RoomEquipmentService} from "../../../service/room-equipment.service";
import {EquipmentRequestDto} from "../../../model/equipmentRequestDto";
import {EquipmentResponseDto} from "../../../model/equipmentResponseDto";
import {Router} from "@angular/router";

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
  roomNumbersList: number[] = [];

  @Input()
  equipmentEditId: number = -1;

  @Input()
  public equipments: EquipmentResponseDto[] = [];


  name = new FormControl();
  description = new FormControl();
  roomNumber = new FormControl();


  private subscriptions: Subscription[] = [];

  constructor(private equipmentService: RoomEquipmentService,
              private notifier: NotificationService,
              private router: Router) {
  }

  ngOnInit(): void {
    /**
     * set initial values in form
     */
    if (this.option === 'EDIT') {
      for (let n = 0; n < this.equipments.length; n++) {
        if (this.equipments[n].id === this.equipmentEditId) {
          this.name.setValue(this.equipments[n].name);
          this.description.setValue(this.equipments[n].description);
          this.roomNumber.setValue(this.equipments[n].roomNumber);
          break;
        }
      }
    }
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
          window.location.reload();
        },
        (httpErrorResponse: HttpErrorResponse) => {

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

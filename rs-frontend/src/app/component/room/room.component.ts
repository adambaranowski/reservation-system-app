import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {EquipmentResponseDto} from "../../model/equipmentResponseDto";
import {HttpErrorResponse} from "@angular/common/http";
import {NotificationType} from "../../enum/notification-type";
import {RoomResponseDto} from "../../model/roomResponseDto";
import {Subscription} from "rxjs";
import {RoomEquipmentService} from "../../service/room-equipment.service";
import {NotificationService} from "../../service/notification.service";

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.css']
})
export class RoomComponent implements OnInit, OnDestroy {

  public OPTION_ADD = 'ADD';
  public OPTION_EDIT = 'EDIT';

  public roomNumbers: number[] = [];

  private subscriptions: Subscription[] = [];
  public rooms: RoomResponseDto[] = [];

  public displayEditForm = false;
  public displayAddForm = false;

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private roomEquipmentService: RoomEquipmentService,
              private notifier: NotificationService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.reloadRooms()
    } else {
      this.router.navigateByUrl('/login')
    }
  }


  public reloadRooms(): void {
    this.subscriptions.push(this.roomEquipmentService.getAllRooms().subscribe(
      (response: RoomResponseDto[]) => {
        this.rooms = response;

      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR, 'Cannot get Rooms' + httpErrorResponse.error);
      }
    ))
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  deleteRoom(roomId: number | undefined): void {
    if (roomId) {
      this.subscriptions.push(this.roomEquipmentService.deleteRoom(roomId).subscribe(
        (response: any) => {
          this.notifier.showNotification(NotificationType.WARNING, 'Room deleted');
          this.reloadRooms();
        },
        (httpErrorResponse: HttpErrorResponse) => {
          console.log(httpErrorResponse);
          this.notifier.showNotification(NotificationType.ERROR, httpErrorResponse.error);
        }
      ))
    }
  }

  goToEquipment(): void {
    this.router.navigateByUrl('/menu/equipment');
  }

  updateRoomNumbers() {
    this.roomNumbers = [];
    this.subscriptions.push(this.roomEquipmentService.getAllRooms().subscribe(
      (response: RoomResponseDto[]) => {

        response.forEach(room => {

          const roomNumber = room.roomNumber;

          if (roomNumber) {
            this.roomNumbers.push(<number>roomNumber)
          }
        })

        this.reloadRooms();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR, httpErrorResponse.error);
      }
    ))
  }


}

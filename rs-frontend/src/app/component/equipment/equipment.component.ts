import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {NotificationService} from "../../service/notification.service";
import {HttpErrorResponse} from "@angular/common/http";
import {NotificationType} from "../../enum/notification-type";
import {RoomEquipmentService} from "../../service/room-equipment.service";
import {EquipmentResponseDto} from "../../model/equipmentResponseDto";
import {RoomResponseDto} from "../../model/roomResponseDto";

@Component({
  selector: 'app-equipment',
  templateUrl: './equipment.component.html',
  styleUrls: ['./equipment.component.css']
})
export class EquipmentComponent implements OnInit, OnDestroy {
  public OPTION_ADD = 'ADD';
  public OPTION_EDIT = 'EDIT';

  public roomNumbers: number[] = [];

  private subscriptions: Subscription[] = [];
  public equipments: EquipmentResponseDto[] = [];

  public displayEditForm = false;
  public displayAddForm = false;

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private roomEquipmentService: RoomEquipmentService,
              private notifier: NotificationService) {
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.reloadEquipments();
      this.updateRoomNumbers();
    } else {
      this.router.navigateByUrl('/login')
    }
  }

  public reloadEquipments(): void {
    this.subscriptions.push(this.roomEquipmentService.getAllEquipments().subscribe(
      (response: EquipmentResponseDto[]) => {
        this.equipments = response;

      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR, 'Cannot get Equipments' + httpErrorResponse.error);
      }
    ))
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  //
  // enableUser(user: User): void {
  //   this.subscriptions.push(this.userService.enableUsers([user.id]).subscribe(
  //     (response: any ) => {
  //       this.notifier.showNotification(NotificationType.INFO, 'User enabled');
  //       this.reloadUsers();
  //     },
  //     (httpErrorResponse: HttpErrorResponse) => {
  //       console.log(httpErrorResponse);
  //       this.notifier.showNotification(NotificationType.ERROR,  httpErrorResponse.error);
  //     }
  //   ))
  // }
  //
  // disableUser(user: User): void {
  //   this.subscriptions.push(this.userService.disableUsers([user.id]).subscribe(
  //     (response: any ) => {
  //       this.notifier.showNotification(NotificationType.WARNING, 'User disabled');
  //       this.reloadUsers();
  //     },
  //     (httpErrorResponse: HttpErrorResponse) => {
  //       console.log(httpErrorResponse);
  //       this.notifier.showNotification(NotificationType.ERROR,  httpErrorResponse.error);
  //     }
  //   ))
  // }
  //
  deleteEquipment(eqId: number | undefined): void {
    if (eqId) {
      this.subscriptions.push(this.roomEquipmentService.deleteEquipment(eqId).subscribe(
        (response: any) => {
          this.notifier.showNotification(NotificationType.WARNING, 'Equipment deleted');
          this.reloadEquipments();
        },
        (httpErrorResponse: HttpErrorResponse) => {
          console.log(httpErrorResponse);
          this.notifier.showNotification(NotificationType.ERROR, httpErrorResponse.error);
        }
      ))
    }
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

        this.reloadEquipments();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR, httpErrorResponse.error);
      }
    ))
  }

}

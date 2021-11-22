import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../service/authentication.service";
import {User} from "../../model/User";
import {RoomResponseDto} from "../../model/roomResponseDto";
import {RoomEquipmentService} from "../../service/room-equipment.service";
import {HttpErrorResponse} from "@angular/common/http";
import {NotificationType} from "../../enum/notification-type";
import {Subscription} from "rxjs";
import {NotificationService} from "../../service/notification.service";
import {ReservationService} from "../../service/reservation.service";
import {SingleReservationDto} from "../../model/singleReservationDto";
import {GetReservationRequestDto} from "../../model/getReservationRequestDto";

@Component({
  selector: 'app-reservation',
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.css']
})
export class ReservationComponent implements OnInit, OnDestroy {

  public user?: User;
  public rooms: RoomResponseDto[] = [];
  public pickedRoomNumber = null;

  public today: Date = new Date();
  public firstDayOfWeek: Date;
  public lastDayOfWeek: Date;

  public  hours = ['8:00', '9:00', '10:00', '11:00', '12:00',
    '13:00', '14:00', '15:00', '16:00', '17:00',
    '18:00', '19:00', '20:00'];

  public days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']

  private subscriptions: Subscription[] = [];

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private roomEquipmentService: RoomEquipmentService,
              private reservationService: ReservationService,
              private notifier: NotificationService) {
    let daysToBeginOfWeek = this.today.getDay() - 1; // -1 because in calendar weekStart is on Mon not Sun
    let daysToEndOfWeek = 7 - this.today.getDay() - 2;
    this.firstDayOfWeek = this.addDays(new Date(this.today), -daysToBeginOfWeek);
    this.lastDayOfWeek = this.addDays(new Date(this.today), daysToEndOfWeek);
  }

  updateReservations(roomNumber: number | undefined): void {

    let beginMonth = this.firstDayOfWeek.getMonth().toString().length < 2 ?
      '0' + this.firstDayOfWeek.getMonth() : this.firstDayOfWeek.getMonth();

    let endMonth = this.lastDayOfWeek.getMonth().toString().length < 2 ?
      '0' + this.lastDayOfWeek.getMonth() : this.lastDayOfWeek.getMonth();

    let beginDateString = this.firstDayOfWeek.getDate() + '-' +
      beginMonth + '-' +
      + this.firstDayOfWeek.getFullYear();

    let endDateString = this.lastDayOfWeek.getDate() + '-' +
      endMonth + '-' +
      + this.lastDayOfWeek.getFullYear();

    const reservationRequestDto: GetReservationRequestDto = {
      beginDate: beginDateString,
      endDate: endDateString,
      roomNumber: roomNumber
    }

    this.subscriptions.push(this.reservationService.getAllReservationsForPeriod(reservationRequestDto).subscribe(
      (response: SingleReservationDto[]) => {
        response.forEach(r => console.log(r))

      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse);
        this.notifier.showNotification(NotificationType.ERROR, 'Cannot get reservations: ' + httpErrorResponse.error);
      }
    ))
  }

  addDays(date: Date, days: number): Date {
    date.setDate(date.getDate() + days);
    return date;
  }

  nextWeek(): void {
    this.firstDayOfWeek.setDate(this.addDays(this.firstDayOfWeek, 7).getDate())
    this.lastDayOfWeek.setDate(this.addDays(this.lastDayOfWeek, 7).getDate())
  }

  previousWeek(): void {
    this.firstDayOfWeek.setDate(this.addDays(this.firstDayOfWeek, -7).getDate())
    this.lastDayOfWeek.setDate(this.addDays(this.lastDayOfWeek, -7).getDate())
  }

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.reloadRooms();
    } else {
      this.router.navigateByUrl('/login')
    }

    this.user = this.authenticationService.getUser();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
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

}

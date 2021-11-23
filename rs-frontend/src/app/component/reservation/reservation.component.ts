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
import {ReservationCell} from "./ReservationCell";

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

  public reservationsGrid: ReservationCell[][] = [];

  fillReservationGridWithReservations(reservations: SingleReservationDto[]): void {
    this.fillReservationGrid();
    reservations.forEach(reservation => {

      //dd-mm-yyyy
      let splitDate = reservation.date.split('-');

      //yyyy-mm-dd
      let reservationDate = new Date(parseInt(splitDate[2]), parseInt(splitDate[1])-1, parseInt(splitDate[0]));

      let day = reservationDate.getDay()-1; // -1 because Monday is first instead of Sunday
      let beginHour = parseInt(reservation.beginTime.split('-')[0]);


      if (day >= 0 && day <= 4){
        if (beginHour >= 8 && beginHour <= 20) {
          this.reservationsGrid[day][beginHour-8].reservations.push(reservation);
        }
      }

    })
  }

  getReservationsForCell(day: number, hour: number): SingleReservationDto[] {
    return this.reservationsGrid[day][hour].reservations;
  }

  fillReservationGrid(): void {

    this.reservationsGrid = [];

    for (let day = 0; day <= 4; day++) {
      const dayGrid = [];
      for (let hour = 8; hour <= 20; hour++){

          const oneHourCell: ReservationCell = {
            day: day,
            beginHour: hour,
            reservations: []
          };

          dayGrid.push(oneHourCell);
      }
      this.reservationsGrid.push(dayGrid);
    }
  }

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

    let beginDay = this.firstDayOfWeek.getDate() < 10 ? '0'+this.firstDayOfWeek.getDate() : this.firstDayOfWeek.getDate();

    let beginMonth = this.firstDayOfWeek.getMonth() < 9 ?
      '0' + (this.firstDayOfWeek.getMonth()+1) : (this.firstDayOfWeek.getMonth()+1);

    let endMonth = this.lastDayOfWeek.getMonth() < 9 ?
      '0' + (this.lastDayOfWeek.getMonth()+1) : this.lastDayOfWeek.getMonth()+1;

    let endDay = this.lastDayOfWeek.getDate() < 10 ? '0'+this.lastDayOfWeek.getDate() : this.lastDayOfWeek.getDate();

    let beginDateString = beginDay + '-' +
      beginMonth + '-' +
      + this.firstDayOfWeek.getFullYear();


    let endDateString = endDay + '-' +
      endMonth + '-' +
      + this.lastDayOfWeek.getFullYear();

    const reservationRequestDto: GetReservationRequestDto = {
      beginDate: beginDateString,
      endDate: endDateString,
      roomNumber: roomNumber
    }

    this.subscriptions.push(this.reservationService.getAllReservationsForPeriod(reservationRequestDto).subscribe(
      (response: SingleReservationDto[]) => {

        this.fillReservationGridWithReservations(response);

        console.log(this.reservationsGrid);
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
    this.fillReservationGrid();
    console.log(this.reservationsGrid);
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

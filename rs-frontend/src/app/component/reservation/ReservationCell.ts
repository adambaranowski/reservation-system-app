import {SingleReservationDto} from "../../model/singleReservationDto";

export interface ReservationCell {
  day: number,
  beginHour: number,
  reservations: SingleReservationDto[]
}

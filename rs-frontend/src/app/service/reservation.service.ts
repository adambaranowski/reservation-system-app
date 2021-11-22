import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {GetReservationRequestDto} from "../model/getReservationRequestDto";
import {Observable} from "rxjs";
import {SingleReservationDto} from "../model/singleReservationDto";
import {EquipmentResponseDto} from "../model/equipmentResponseDto";
import {Urls} from "../enum/urls";
import {CreateReservationRequestDto} from "../model/createReservationRequestDto";

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private host: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }


  public getAllReservationsForPeriod(getReservationRequestDto: GetReservationRequestDto): Observable<Array<SingleReservationDto>> {
    return this.httpClient
      .post<Array<SingleReservationDto>>
      (`${this.host}/${Urls.RESERVATIONS}/forPeriod/getAll`, getReservationRequestDto);
  }

  public addNewReservation(createReservationRequestDto: CreateReservationRequestDto): Observable<any | HttpErrorResponse> {
    return this.httpClient
      .post<any | HttpErrorResponse>
      (`${this.host}/${Urls.RESERVATIONS}`, createReservationRequestDto);
  }

  public deleteReservation(reservationId: number): Observable<any | HttpErrorResponse> {
    return this.httpClient
      .delete<any | HttpErrorResponse>
      (`${this.host}/${Urls.RESERVATIONS}/${reservationId}`);
  }
}

import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {RoomResponseDto} from "../model/roomResponseDto";
import {Urls} from "../enum/urls";
import {Observable} from "rxjs";
import {RoomRequestDto} from "../model/roomRequestDto";
import {EquipmentResponseDto} from "../model/equipmentResponseDto";
import {EquipmentRequestDto} from "../model/equipmentRequestDto";

@Injectable({
  providedIn: 'root'
})
export class RoomEquipmentService {

  private host: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }


  // ROOMS
  public addRoom(roomRequestDto: RoomRequestDto): Observable<RoomResponseDto> {
    return this.httpClient
      .post<RoomResponseDto>
      (`${this.host}/${Urls.ROOMS}`, roomRequestDto);
  }

  public modifyRoom(roomRequestDto: RoomRequestDto, roomId: number): Observable<RoomResponseDto | HttpErrorResponse> {
    return this.httpClient
      .put<RoomResponseDto | HttpErrorResponse>
      (`${this.host}/${Urls.ROOMS}/${roomId}`, roomRequestDto);
  }

  public deleteRoom(roomId: number): Observable<any | HttpErrorResponse> {
    return this.httpClient
      .delete<any | HttpErrorResponse>
      (`${this.host}/${Urls.ROOMS}/${roomId}`);
  }

  public getAllRooms(): Observable<Array<RoomResponseDto>> {
    return this.httpClient
      .get<Array<RoomResponseDto>>
      (`${this.host}/${Urls.ROOMS}`);
  }

  public getRoomById(roomId: number): Observable<RoomResponseDto | HttpErrorResponse> {
    return this.httpClient
      .get<RoomResponseDto | HttpErrorResponse>
      (`${this.host}/${Urls.ROOMS}/${roomId}`);
  }

 //EQUIPMENT

  public getAllEquipments(): Observable<Array<EquipmentResponseDto>> {
    return this.httpClient
      .get<Array<EquipmentResponseDto>>
      (`${this.host}/${Urls.EQUIPMENT}`);
  }

  public getEquipmentById(equipmentId: number): Observable<EquipmentResponseDto> {
    return this.httpClient
      .get<EquipmentResponseDto>
      (`${this.host}/${Urls.EQUIPMENT}/${equipmentId}`);
  }

  public addEquipment(equipmentRequestDto: EquipmentRequestDto): Observable<EquipmentResponseDto> {
    return this.httpClient
      .post<EquipmentResponseDto>
      (`${this.host}/${Urls.EQUIPMENT}`, equipmentRequestDto);
  }

  public modifyEquipment(equipmentRequestDto: EquipmentRequestDto, equipmentId: number): Observable<EquipmentResponseDto> {
    return this.httpClient
      .put<EquipmentResponseDto>
      (`${this.host}/${Urls.EQUIPMENT}/${equipmentId}`, equipmentRequestDto);
  }

  public deleteEquipment(equipmentId: number): Observable<any> {
    return this.httpClient
      .delete<any>
      (`${this.host}/${Urls.EQUIPMENT}/${equipmentId}`);
  }



}

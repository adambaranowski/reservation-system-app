import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {User} from "../model/User";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {UserRequestDto} from "../model/UserRequestDto";
import {Urls} from "../enum/urls";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private host: string = environment.apiUrl;

  constructor(private httpClient: HttpClient) { }

  public register(registerDto: UserRequestDto): Observable<User> {
    return this.httpClient
      .post<User>
      (`${this.host}/${Urls.USERS}`, registerDto);
  }

  public getUsers(): Observable<User[]> {
    return this.httpClient
      .get<User[]>
      (`${this.host}/${Urls.USERS}`);
  }

  public getUserById(userId: number): Observable<User> {
    return this.httpClient
      .get<User>
      (`${this.host}/${Urls.USERS}/${userId}`);
  }


  public disableUsers(usersIds: [number]): Observable<any | HttpErrorResponse>{
    return this.httpClient.post<void>(`${this.host}/${Urls.USERS}/disable`, usersIds);
  }

  public enableUsers(usersIds: [number]): Observable<any | HttpErrorResponse>{
    return this.httpClient.post<void>(`${this.host}/${Urls.USERS}/enable`, usersIds);
  }

  public modifyUser(userId: number, requestDto: UserRequestDto): Observable<User> {
    return this.httpClient.put<User>(`${this.host}/${Urls.USERS}/${userId}`, requestDto);
  }

  public deleteUser(userId: number): Observable<any | HttpErrorResponse> {
    return this.httpClient.delete<User | HttpErrorResponse>(`${this.host}/${Urls.USERS}/${userId}`);
  }
}

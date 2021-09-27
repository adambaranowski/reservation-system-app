export interface UserLoginResponseDto {
  token: string;
  userId: number;
  email: string;
  userNick: string;
  authorities: [];


  // constructor(token: string) {
  //   this.token = token;
  // }
}

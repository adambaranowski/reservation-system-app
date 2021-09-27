export interface UserRequestDto {
  authorities: [string | null];
  email: string;
  password: string;
  userNick: string;


  // constructor(authorities: [string], email: string, password: string, userNick: string) {
  //   this.authorities = authorities;
  //   this.email = email;
  //   this.password = password;
  //   this.userNick = userNick;
  // }
}

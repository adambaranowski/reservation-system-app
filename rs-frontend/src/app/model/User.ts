export interface User {
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  authorities: [string];
  credentialsNonExpired: boolean;
  email: string;
  id: number;
  joinDateTime: string;
  lastLoginDateTime: string;
  userNick: string;


  // constructor(accountNonExpired: boolean, accountNonLocked: boolean, authorities: [string], credentialsNonExpired: boolean, email: string, id: number, joinDateTime: string, lastLoginDateTime: string, userNick: string) {
  //   this.accountNonExpired = accountNonExpired;
  //   this.accountNonLocked = accountNonLocked;
  //   this.authorities = authorities;
  //   this.credentialsNonExpired = credentialsNonExpired;
  //   this.email = email;
  //   this.id = id;
  //   this.joinDateTime = joinDateTime;
  //   this.lastLoginDateTime = lastLoginDateTime;
  //   this.userNick = userNick;
  // }
}

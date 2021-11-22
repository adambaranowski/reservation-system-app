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

}

import {UserRole} from "./User";

export class UserTokenState {
  accessToken: string = "";
  expiresIn: number = 10000;
}

export class LoginResponseDto {
  id!: any;
  expiresAt!: number;
  role!: string;
}

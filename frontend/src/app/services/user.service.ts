import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";
import {User, UserDetails} from '../model/User';
import {environment} from "../environment.development";
import {AuthService} from "./auth.service";
import {CreateUserCredentials} from "../model/RegisterCredentials";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly userUrl: string;

  constructor(private http: HttpClient) {
    this.userUrl = environment.authUrl + '/user';
  }

  public getLawyers(): Observable<User[]> {
    return this.http.get<User[]>(this.userUrl, AuthService.getHttpOptions());
  }

  public getLawyer(userEmail: string): Observable<UserDetails> {
    return this.http.get<UserDetails>(this.userUrl + '/' + userEmail, AuthService.getHttpOptions());
  }

  public deleteUser(email: string): Observable<boolean> {
    return this.http.delete<boolean>(this.userUrl + "/" + email, AuthService.getHttpOptions())
  }

  public editLawyer(requestBody: CreateUserCredentials) {
    return this.http.put<string>(this.userUrl, requestBody, AuthService.getHttpOptions());
  }

  public getSubscriptions(): Observable<string[]> {
    return this.http.get<string[]>(this.userUrl + '/subscriptions/' + localStorage.getItem("id"), AuthService.getHttpOptions());
  }

  public setSubscriptions(subscriptions: string[]): Observable<void> {
    const body = {
      'services': subscriptions,
      'userId': localStorage.getItem("id")
    }
    return this.http.post<void>(this.userUrl + '/subscriptions', body, AuthService.getHttpOptions());
  }
}

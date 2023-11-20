import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../environment.development";
import {Observable} from "rxjs";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private readonly paymentUrl: string;

  constructor(private http: HttpClient) {
    this.paymentUrl = environment.apiUrl + '/api/payment';
  }

  public subscribe(): Observable<string> {
    return this.http.get<string>(this.paymentUrl, AuthService.getHttpOptions());
  }

  public remove(): Observable<string> {
    return this.http.get<string>(this.paymentUrl, AuthService.getHttpOptions());
  }

  public proceedPayment(method: string, req: any): Observable<any> {
    const url = `${this.paymentUrl}/${method}`;
    return this.http.post<any>(url, req, AuthService.getHttpOptions());
  }
}

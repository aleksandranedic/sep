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

  public save(services: string[]): Observable<void> {
    return this.http.post<void>(this.paymentUrl, services, AuthService.getHttpOptions());
  }

  public proceedPayment(method: string, req: any, path = "api/payment"): Observable<any> {
    const url = `${this.paymentUrl}/proceed`;
    return this.http.post<any>(url, {...req, path:`/${method}/${path}`}, AuthService.getHttpOptions());
  }

  public confirmPaypalPayment(paymentId: string, payerId: string) {
    const url = `${this.paymentUrl}/proceed`;
    return this.http.post<any>(url, {paymentId, payerId,  path:`/paypal/api/payment/confirm}`}, AuthService.getHttpOptions());
  }

  public getPlanId(payload: any): Observable<any> {
    const url = `${this.paymentUrl}/proceed`;
    return this.http.post<any>(url, {...payload, path: '/paypal/api/payment/subscribe'}, AuthService.getHttpOptions());
  }

  public checkCryptoTransaction(transactionId: string) {
    const url = `${this.paymentUrl}/proceed`;
    return this.http.post<any>(url, {transactionId, path: '/crypto/api/payment/status'}, AuthService.getHttpOptions());
  }
}

import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../environment.development";
import {Observable} from "rxjs";
import {AuthService} from "./auth.service";
import {
  CardPaymentDTO,
  CardPaymentResponseDTO,
  PaymentResponseDTO,
  PCCResponseDTO,
  PSPPaymentDTO
} from "../model/BankDtos";

@Injectable({
  providedIn: 'root'
})
export class BankService {

  private readonly bankUrl: string;

  constructor(private http: HttpClient) {
    this.bankUrl = environment.bankUrl + '/api/bank';
  }

  public pay(method: string, pspPaymentDTO: PSPPaymentDTO): Observable<PaymentResponseDTO> {
    const url = `${this.bankUrl}/pay/${method}/`;
    return this.http.post<PaymentResponseDTO>(url, pspPaymentDTO, AuthService.getHttpOptions());
  }

  public payWithCardQR(method: string, cardPaymentDTO: CardPaymentDTO): Observable<CardPaymentResponseDTO> {
    const url = `${this.bankUrl}/${method}/payment`;
    return this.http.post<CardPaymentResponseDTO>(url, cardPaymentDTO);
  }

  public issuerPay(pccPayloadDTO: any): Observable<PCCResponseDTO> {
    const url = `${this.bankUrl}/payment/issuer`;
    return this.http.post<PCCResponseDTO>(url, pccPayloadDTO, AuthService.getHttpOptions());
  }
}

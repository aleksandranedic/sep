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

  public payWithCard(cardPaymentDTO: CardPaymentDTO): Observable<CardPaymentResponseDTO> {
    const url = `${this.bankUrl}/card/payment`;
    return this.http.post<CardPaymentResponseDTO>(url, cardPaymentDTO);
  }

  public payWithQR(qrCode: string): Observable<CardPaymentResponseDTO> {
    const url = `${this.bankUrl}/qr/payment`;
    return this.http.post<CardPaymentResponseDTO>(url, qrCode);
  }

  public issuerPay(pccPayloadDTO: any): Observable<PCCResponseDTO> {
    const url = `${this.bankUrl}/payment/issuer`;
    return this.http.post<PCCResponseDTO>(url, pccPayloadDTO, AuthService.getHttpOptions());
  }
}

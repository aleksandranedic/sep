import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class QrCodeService {

  private apiUrl = 'https://nbs.rs/QRcode/api/qr/v1/gen';

  constructor(private http: HttpClient) {}

  public generateQRCode(body: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<any>(this.apiUrl, body, { headers });
  }
}

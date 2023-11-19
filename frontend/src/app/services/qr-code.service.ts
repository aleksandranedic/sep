import { Injectable } from '@angular/core';
import QRCode from "qrcode";

@Injectable({
  providedIn: 'root'
})
export class QrCodeService {
  generateQrCode(url: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      QRCode.toDataURL(url, (error, dataUrl) => {
        if (error) {
          reject(error);
        } else {
          resolve(dataUrl);
        }
      });
    });
  }
}

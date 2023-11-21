import {Component, OnInit} from '@angular/core';
import {QrCodeService} from "../../../../services/qrcode.service";

@Component({
  selector: 'app-qr-code',
  template: `
    <div *ngIf="qrCodeDataUrl">
      <img [src]="qrCodeDataUrl" alt="QR Code">
    </div>
  `,
  styles: [
    `
      img {
        max-width: 100%;
        height: auto;
      }
    `
  ]
})
export class QrCodeComponent implements OnInit {
  qrCodeDataUrl: string | null = null;

  constructor(private yourService: QrCodeService) {}

  ngOnInit(): void {
    this.generateQRCode();
  }

  generateQRCode(): void {
    const requestParams = {
      "K": "PR",
      "V": "01",
      "C": "1",
      "R": "845000000040484987",
      "N": "JP EPS BEOGRAD\r\nBALKANSKA 13",
      "I": "RSD3596,13",
      "P": "MRĐO MAČKATOVIĆ\r\nŽUPSKA 13\r\nBEOGRAD 6",
      "SF": "189",
      "S": "UPLATA PO RAČUNU ZA EL. ENERGIJU",
      "RO": "97163220000111111111000"
    };

    this.yourService.generateQRCode(requestParams).subscribe(
      response => {
        console.log('QR Code generated successfully:', response);
        this.qrCodeDataUrl = response.generatedQrCode; // Adjust the response property based on the actual API response
      },
      error => {
        console.error('Error generating QR Code:', error);
        // Handle the error as needed
      }
    );
  }
}

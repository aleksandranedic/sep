import {Component, Input, OnInit} from '@angular/core';
import * as QRCode from 'qrcode';

@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.component.html',
  styleUrls: ['./qr-code.component.css']
})
export class QrCodeComponent implements OnInit {
  @Input() receiverAccount: string = '';
  @Input() receiverName: string = '';
  @Input() currency: string = '';
  @Input() amount: string = '';
  @Input() payerCity: string = '';
  @Input() paymentCode: string = '';
  @Input() paymentPurpose: string = '';

  ngOnInit() {
    this.generateQRCode();
  }

  generateQRCode() {
    const paymentData = this.getPaymentData();
    const canvas = document.getElementById('qrcode') as HTMLCanvasElement;

    QRCode.toCanvas(canvas, paymentData, function (error: any) {
      if (error) console.error(error);
      console.log('QR Code generated successfully');
    });
  }

  getPaymentData(): string {
    if (
      this.receiverAccount &&
      this.receiverName &&
      this.currency &&
      this.amount &&
      this.payerCity &&
      this.paymentCode &&
      this.paymentPurpose
    ) {
      return `K:PR|V:01|C:1|R:${this.receiverAccount}|N:${this.receiverName}|I:${this.currency}${this.amount}|P:${this.payerCity}|SF:${this.paymentCode}|S:${this.paymentPurpose}`;
    } else {
      console.error('Missing required input fields');
      return '';
    }
  }
}

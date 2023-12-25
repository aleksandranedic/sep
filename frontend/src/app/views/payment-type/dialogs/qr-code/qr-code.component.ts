import {Component, Input, OnInit} from '@angular/core';
import * as QRCode from 'qrcode';
import {BrowserQRCodeReader} from '@zxing/browser';
import {CardPaymentResponseDTO} from "../../../../model/BankDtos";
import {Router} from "@angular/router";
import {BankService} from "../../../../services/bank.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DialogRef} from "@angular/cdk/dialog";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.component.html',
  styleUrls: ['./qr-code.component.css']
})
export class QrCodeComponent implements OnInit {
  @Input() receiverAccount: string = '';
  @Input() receiverName: string = '';
  @Input() currency: string = '';
  @Input() amount: number = 0;
  @Input() payerCity: string = '';
  @Input() paymentCode: string = '';
  @Input() paymentPurpose: string = '';
  @Input() dialogRef: MatDialogRef<QrCodeComponent> | undefined;

  constructor(private route: Router, private bankService: BankService, private _snackBar: MatSnackBar) {
  }

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

  scan() {
    const codeReader = new BrowserQRCodeReader();
    const qrCodeResult = codeReader.decodeFromCanvas(<HTMLCanvasElement>document.getElementById('qrcode'));
    console.log(qrCodeResult.getText())
    this.pay(qrCodeResult.getText())
  }

  pay(qrCode: string) {
    this.bankService.payWithQR(qrCode).subscribe({
      next: (value: CardPaymentResponseDTO) => {
        this.dialogRef?.close();
        this.route.navigate([value.redirectionUrl]);
      },
      error: (err) => {
        console.log(err);
        this.route.navigate([err.redirectionUrl]);
      }
    });
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, '', {
      duration: 3000,
      panelClass: ['snack-bar']
    })
  }
}

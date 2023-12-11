import {Component, Input} from '@angular/core';
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {PaymentService} from "../../../services/payment.service";
import {QrCodeComponent} from "../dialogs/qr-code/qr-code.component";

@Component({
  selector: 'app-payment-type',
  templateUrl: './payment-type.component.html',
  styleUrls: ['./payment-type.component.css']
})
export class PaymentTypeContainer {

  @Input() price = 0;

  req = {
    "merchantId": "655bc6821c76400a7ecc8722",
    "merchantPassword": "lala",
    "amount": 350
  };

  constructor(private route: Router, public dialog: MatDialog, private paymentService: PaymentService) {
  }

  goToPage(path: string) {
    this.route.navigate([path]);
  }

  creditCard() {
    this.paymentService.proceedPayment("card", this.req).subscribe({
      next: (value: any) => {
        const url = this.route.serializeUrl(
          this.route.createUrlTree([value["paymentUrl"].slice(22) + '/' + value["paymentId"]])
        );
        window.open(url, '_blank');
      },
      error: (err) => console.log(err)
    })
  }

  qrCode() {
    let dialogRef = this.dialog.open(QrCodeComponent);
    dialogRef.componentInstance.receiverAccount = '105000000000000029';
    dialogRef.componentInstance.receiverName = 'pera';
    dialogRef.componentInstance.currency = 'RSD';
    dialogRef.componentInstance.amount = '10,00';
    dialogRef.componentInstance.payerCity = 'Beograd';
    dialogRef.componentInstance.paymentCode = '221';
    dialogRef.componentInstance.paymentPurpose = 'test';
  }

  crypto() {
    this.paymentService.proceedPayment("crypto", this.req).subscribe({
      next: (value: any) => {
        console.log(value);
      },
      error: (err) => console.log(err)
    })
  }

  paypal() {
    this.paymentService.proceedPayment("paypal", this.req).subscribe({
      next: (value: any) => {
        console.log(value);
      },
      error: (err) => console.log(err)
    })
  }
}

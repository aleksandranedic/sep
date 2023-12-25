import {Component, Inject, Input} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {PaymentService} from "../../../services/payment.service";
import {QrCodeComponent} from "../dialogs/qr-code/qr-code.component";
import { PaypalSubComponent } from '../dialogs/paypal-sub/paypal-sub.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-payment-type',
  templateUrl: './payment-type.component.html',
  styleUrls: ['./payment-type.component.css']
})
export class PaymentTypeContainer {

  @Input() price = 0;
  @Input() services: string[] = []

  req = {
    "merchantId": "655bc6821c76400a7ecc8722",
    "merchantPassword": "lala",
    "amount": 350
  };

  constructor(@Inject(MatSnackBar) private _snackBar: MatSnackBar, private actRoute: ActivatedRoute, private route: Router, public dialog: MatDialog, private paymentService: PaymentService) {
  }

  goToPage(path: string) {
    this.route.navigate([path]);
  }

  creditCard() {
    this.paymentService.proceedPayment("mbank", this.req, "api/bank/pay/card").subscribe({
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
    let dialogRef = this.dialog.open(QrCodeComponent, {
      width: '400px'
    });
    dialogRef.componentInstance.receiverAccount = '105000000000000029';
    dialogRef.componentInstance.receiverName = 'pera';
    dialogRef.componentInstance.currency = 'RSD';
    dialogRef.componentInstance.amount = this.price;
    dialogRef.componentInstance.payerCity = 'Beograd';
    dialogRef.componentInstance.paymentCode = '221';
    dialogRef.componentInstance.paymentPurpose = 'subscription';
  }

  crypto() {
    this.paymentService.proceedPayment("crypto", {amount: this.price, email: localStorage.getItem('email')}).subscribe({
      next: (res: any) => {
        this._snackBar.open(`Send ${res.bitcoins} bitcoins to this address: ${res.address}`, '', {
          duration: 15000
        })
        const timer = setInterval(() => {
          this.paymentService.checkCryptoTransaction(res.transactionId).subscribe(res => {
            console.log(res)
            if (res.status === "SUCCESS") {
              this._snackBar.open("Payment successuful", '', {
                duration: 10000
              })
              clearInterval(timer);
            }
          })
        }, 2000);
      },
      error: (err) => console.log(err)
    })
  }

  paypal() {
    this.paymentService.proceedPayment("paypal", {amount: this.price}).subscribe({
      next: (value: any) => {
        window.open(value.redirectURL, '_blank');
        console.log(value);
      },
      error: (err) => console.log(err)
    })
  }

  paypalSubscribe() {
    const payload = this.generateSubPayload();
    const observable = this.paymentService.getPlanId(payload);
    observable.subscribe(response => {
      let dialogRef = this.dialog.open(PaypalSubComponent, {
        width: '400px'
      });
      dialogRef.componentInstance.services = this.services;
      dialogRef.componentInstance.amount = this.price;
      dialogRef.componentInstance.planId = response.planId;
    })
  }

  generateSubPayload() {
    return {
       internet: this.services.filter(name => name.toLowerCase().includes("internet")).length > 0,
       digital: this.services.filter(name => name.toLowerCase().includes("electronic ")).length > 0,
       printed: this.services.filter(name => name.toLowerCase().includes("printed")).length > 0,
       codification: this.services.filter(name => name.toLowerCase().includes("codification")).length > 0,
       monthly: this.price < 200,
       amount: this.price
    }
  }
}

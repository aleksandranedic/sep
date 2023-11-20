import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {CreditCardDialogComponent} from "../dialogs/credit-card-dialog/credit-card-dialog.component";
import {PaymentService} from "../../../services/payment.service";

@Component({
  selector: 'app-payment-type',
  templateUrl: './payment-type.component.html',
  styleUrls: ['./payment-type.component.css']
})
export class PaymentTypeContainer {

  constructor(private route: Router, public dialog: MatDialog, private paymentService: PaymentService) {
  }

  goToPage(path: string) {
    this.route.navigate([path]);
  }

  creditCard() {
    let req = {
      "merchantId": "655a84637287ff66200eeead",
      "merchantPassword": "lala",
      "amount": 350
    };

    this.paymentService.proceedPayment("card", req).subscribe({
      next: (value: any) => {
        const url = this.route.serializeUrl(
          this.route.createUrlTree([value["paymentUrl"].slice(22) + '/' + value["paymentId"]])
        );
        window.open(url, '_blank');
      },
      error: (err) => console.log(err)
    })
  }
}

import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {CreditCardDialogComponent} from "../dialogs/credit-card-dialog/credit-card-dialog.component";

@Component({
  selector: 'app-payment-type',
  templateUrl: './payment-type.component.html',
  styleUrls: ['./payment-type.component.css']
})
export class PaymentTypeContainer {

  constructor(private route: Router, public dialog: MatDialog) {
  }

  goToPage(path: string) {
    this.route.navigate([path]);
  }

  creditCard() {
    let dialogRef = this.dialog.open(CreditCardDialogComponent, {
      height: '650px',
      width: '800px'
    });
  }
}

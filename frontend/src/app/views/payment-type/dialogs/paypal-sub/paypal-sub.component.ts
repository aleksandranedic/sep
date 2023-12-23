import {Component, Inject, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import { MatDialogRef } from '@angular/material/dialog';

declare var paypal: any;

@Component({
  selector: 'app-paypal-sub',
  templateUrl: './paypal-sub.component.html',
  styleUrls: ['./paypal-sub.component.css']
})
export class PaypalSubComponent implements OnInit {
  @Input() amount: number = 0;
  @Input() services: string[] = [];
  @Input() planId: string = '';

  constructor(private dialogRef: MatDialogRef<PaypalSubComponent>, @Inject(MatSnackBar) private _snackBar: MatSnackBar, private route: Router) {
  }

  ngOnInit() {
    paypal.Buttons({
        style: {
          shape: 'rect',
          color: 'gold',
          layout: 'vertical',
          label: 'subscribe'
        },
        createSubscription: (data: any, actions: any) => {
          return actions.subscription.create({
            plan_id: this.planId
          });
        },
        onApprove: (data: any, actions: any) => {
            this._snackBar.open(`Success! ${data.subscriptionID}`, '', {
                duration: 2000
              })
            this.dialogRef.close();
        }
      }).render('#paypal-button-container');
  }
}

import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {PaymentTypeContainer} from "../../../payment-type/container/payment-type.component";

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})
export class StoreComponent {
  services = [
    {name: 'Law Codification', monthlyPrice: 50, yearlyPrice: 500, selected: false},
    {name: 'Printed Law Issuance', monthlyPrice: 30, yearlyPrice: 300, selected: false},
    {name: 'Electronic Law Issuance (PDF)', monthlyPrice: 40, yearlyPrice: 400, selected: false},
    {name: 'Internet Law Issuance', monthlyPrice: 60, yearlyPrice: 600, selected: false}
  ];

  constructor(private router: Router, private dialog: MatDialog) {
  }

  pay(amount: number): void {
    const dialogRef = this.dialog.open(PaymentTypeContainer);
    dialogRef.componentInstance.price = amount;
    const services: string[] = this.services.filter(service => service.selected).map(serv => serv.name);
    dialogRef.componentInstance.services = services;
  }

  payMonthly() {
    const totalPrice = this.services
      .filter(service => service.selected)
      .reduce((sum, service) => sum + (service.selected ? service.monthlyPrice : 0), 0);
    this.pay(totalPrice);
  }

  payYearly() {
    const totalPrice = this.services
      .filter(service => service.selected)
      .reduce((sum, service) => sum + (service.selected ? service.yearlyPrice : 0), 0);
    this.pay(totalPrice);
  }

  disabled(): boolean {
    return this.services.filter(s => s.selected).length === 0;
  }
}

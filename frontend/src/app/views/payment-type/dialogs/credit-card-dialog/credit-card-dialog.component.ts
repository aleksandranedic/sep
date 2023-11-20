import {Component, Input} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {BankService} from "../../../../services/bank.service";
import {CardPaymentDTO} from "../../../../model/BankDtos";

@Component({
  selector: 'app-credit-card-dialog',
  templateUrl: './credit-card-dialog.component.html',
  styleUrls: ['./credit-card-dialog.component.css']
})
export class CreditCardDialogComponent {
  @Input() paymentId: any;

  formGroup = this._formBuilder.group({
    nameFormControl: ['email', [Validators.required]],
    cardNumberFormControl: ['cardNumber', [Validators.required, Validators.minLength(16), Validators.maxLength(16)]],
    expirationDateFormControl: ['expirationDate', [Validators.required]],
    cvvFormControl: ['cvv', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]]
  })

  name = '';
  cvv = '';
  cardNumber = '';
  expirationDate = '';

  constructor(private _formBuilder: FormBuilder, private bankService: BankService) {
  }

  pay() {
    let card = new CardPaymentDTO(this.cardNumber, this.cvv, this.name, this.paymentId, Number(this.expirationDate.split('/')[0]), Number(this.expirationDate.split('/')[1]));
    this.bankService.payWithCardQR("card", card).subscribe({
      next: (value) => console.log("ALL GOOD", value),
      error: (err) => console.log(err)
    });
  }
}

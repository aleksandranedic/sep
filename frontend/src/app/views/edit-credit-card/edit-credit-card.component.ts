import { Component } from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {BankService} from "../../services/bank.service";
import {CardPaymentDTO} from "../../model/BankDtos";

@Component({
  selector: 'app-edit-credit-card',
  templateUrl: './edit-credit-card.component.html',
  styleUrls: ['./edit-credit-card.component.css']
})
export class EditCreditCardComponent {
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

  save() {
    const userId = localStorage.getItem('id') ?? '';
    let card = new CardPaymentDTO(this.cardNumber, this.cvv, this.name, userId, Number(this.expirationDate.split("/")[0]), Number(this.expirationDate.split("/")[1]));
    this.bankService.saveCreditCardData(card).subscribe({
      next: (success) => {
        alert("success: " + success)
      },
      error: (err) => {
        console.log(err);
        alert("error")
      }
    });
  }
}

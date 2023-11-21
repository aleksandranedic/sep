import {Component} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {BankService} from "../../services/bank.service";
import {CardPaymentDTO, CardPaymentResponseDTO} from "../../model/BankDtos";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-pay-card',
  templateUrl: './pay-card.component.html',
  styleUrls: ['./pay-card.component.css']
})
export class PayCardComponent {
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

  constructor(private _formBuilder: FormBuilder, private bankService: BankService, private route: Router, private router: ActivatedRoute) {
  }

  pay() {
    const paymentId = this.router.snapshot.paramMap.get('id') ?? '';
    let card = new CardPaymentDTO(this.cardNumber, this.cvv, this.name, paymentId, Number(this.expirationDate.split("/")[0]), Number(this.expirationDate.split("/")[1]));
    this.bankService.payWithCardQR("card", card).subscribe({
      next: (value: CardPaymentResponseDTO) => {
        this.route.navigate([value.redirectionUrl]);
      },
      error: (err) => {
        console.log(err);
        this.route.navigate([err.redirectionUrl]);
      }
    });
  }
}

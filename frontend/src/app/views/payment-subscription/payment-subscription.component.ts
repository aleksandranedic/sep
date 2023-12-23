import {Component} from '@angular/core';
import {PaymentService} from "../../services/payment.service";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-payment-subscription',
  templateUrl: './payment-subscription.component.html',
  styleUrls: ['./payment-subscription.component.css']
})
export class PaymentSubscriptionComponent {

  subscriptions = [];

  constructor(private paymentService: PaymentService, private authService: AuthService) {
    paymentService.getSubscriptions().subscribe(services => console.log(services));
  }

  services = [
    {
      name: 'Credit Card',
      image: 'assets/images/credit-card.png',
      text: 'Credit cards offer a widely accepted and convenient payment method, allowing users to make purchases and payments with ease. They provide a revolving line of credit, and transactions are often accompanied by various security measures for user protection.',
      selected: false,
      code: 'card'
    },
    {
      name: 'QR Code',
      image: 'assets/images/qr-code.png',
      text: 'QR code payments have gained popularity for their simplicity and speed. Users can make transactions by scanning QR codes, linking directly to their payment information, providing a seamless and efficient way to complete purchases.',
      selected: false,
      code: 'qr'
    },
    {
      name: 'Bitcoin',
      image: 'assets/images/bitcoin.png',
      text: 'Bitcoin, a decentralized digital currency, enables peer-to-peer transactions without the need for intermediaries like banks. Known for its borderless nature and potential for value appreciation, Bitcoin has become a popular choice for those seeking alternatives to traditional currencies.',
      selected: false,
      code: 'crypto'
    },
    {
      name: 'PayPal',
      image: 'assets/images/paypal.png',
      text: 'PayPal serves as a widely used online payment platform, allowing users to link their accounts or credit cards for secure transactions. It provides an additional layer of privacy and is commonly utilized for online purchases, money transfers, and business transactions.',
      selected: false,
      code: 'paypal'
    }
  ];

  save() {
    const selectedServices = this.services.filter(s => s.selected).map(s => s.code);
    console.log(selectedServices);
    this.authService.getCurrentlyLoggedUser().subscribe(res =>
    {
      console.log(res);
      this.paymentService.save(selectedServices).subscribe(_ => alert('Saved!'));
    });
  }
}

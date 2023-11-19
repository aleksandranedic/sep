export class PaymentResponseDTO {
  paymentUrl: string;
  paymentId: string;

  constructor(paymentUrl: string, paymentId: string) {
    this.paymentUrl = paymentUrl;
    this.paymentId = paymentId;
  }
}

export class CardPaymentResponseDTO {
  redirectionUrl: string;
  merchantOrderId: string = '';
  acquirerOrderId: string = '';
  acquirerTimeStamp: string = '';
  paymentId: string = '';

  constructor(redirectionUrl: string) {
    this.redirectionUrl = redirectionUrl;
  }
}

export class PCCResponseDTO {
  public acquirerOrderId: string;
  public acquirerTimestamp: string;
  public issuerOrderId: string;
  public issuerTimestamp: string;
  public amount: number;

  constructor(
    acquirerOrderId: string,
    acquirerTimestamp: string,
    issuerOrderId: string,
    issuerTimestamp: string,
    amount: number
  ) {
    this.acquirerOrderId = acquirerOrderId;
    this.acquirerTimestamp = acquirerTimestamp;
    this.issuerOrderId = issuerOrderId;
    this.issuerTimestamp = issuerTimestamp;
    this.amount = amount;
  }
}

export class PSPPaymentDTO {
  public merchantId: string;
  public merchantPassword: string;
  public amount: number;
  public merchantOrderId: string;
  public merchantTimestamp: string; // Assuming you receive the timestamp as a string
  public successUrl: string;
  public failedUrl: string;
  public errorUrl: string;

  constructor(
    merchantId: string,
    merchantPassword: string,
    amount: number,
    merchantOrderId: string,
    merchantTimestamp: string,
    successUrl: string,
    failedUrl: string,
    errorUrl: string
  ) {
    this.merchantId = merchantId;
    this.merchantPassword = merchantPassword;
    this.amount = amount;
    this.merchantOrderId = merchantOrderId;
    this.merchantTimestamp = merchantTimestamp;
    this.successUrl = successUrl;
    this.failedUrl = failedUrl;
    this.errorUrl = errorUrl;
  }
}

export class CardPaymentDTO {
  public pan: string;
  public securityCode: string;
  public cardHolderName: string;
  public paymentId: string;
  public expiryMonth: number;
  public expiryYear: number;

  constructor(
    pan: string,
    securityCode: string,
    cardHolderName: string,
    paymentId: string,
    expiryMonth: number,
    expiryYear: number
  ) {
    this.pan = pan;
    this.securityCode = securityCode;
    this.cardHolderName = cardHolderName;
    this.paymentId = paymentId;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
  }
}

export class PCCPayloadDTO {
  public cardInfo: CardInfo;
  public amount: number;
  public acquirerOrderId: string;
  public acquirerTimestamp: string;

  constructor(
    cardInfo: CardInfo,
    amount: number,
    acquirerOrderId: string,
    acquirerTimestamp: string
  ) {
    this.cardInfo = cardInfo;
    this.amount = amount;
    this.acquirerOrderId = acquirerOrderId;
    this.acquirerTimestamp = acquirerTimestamp;
  }
}

export class CardInfo {
  public pan: string;
  public securityCode: string;
  public cardHolderName: string;
  public expiryMonth: number;
  public expiryYear: number;

  constructor(
    pan: string,
    securityCode: string,
    cardHolderName: string,
    expiryMonth: number,
    expiryYear: number
  ) {
    this.pan = pan;
    this.securityCode = securityCode;
    this.cardHolderName = cardHolderName;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
  }
}

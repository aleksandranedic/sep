import {NgModule} from "@angular/core";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatCardModule} from "@angular/material/card";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatDialogModule} from "@angular/material/dialog";
import {CommonModule} from "@angular/common";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatMenuModule} from "@angular/material/menu";
import {SharedModule} from "../../shared/shared.module";
import {MatSelectModule} from "@angular/material/select";
import {MatTableModule} from "@angular/material/table";
import {MatTooltipModule} from "@angular/material/tooltip";
import {PaymentTypeContainer} from "./container/payment-type.component";
import { QrCodeComponent } from './dialogs/qr-code/qr-code.component';
import {NgxQRCodeModule} from "@techiediaries/ngx-qrcode";

@NgModule({
  declarations: [
    PaymentTypeContainer,
    QrCodeComponent,
  ],
    imports: [
        MatFormFieldModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatDialogModule,
        FormsModule,
        CommonModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatMenuModule,
        SharedModule,
        MatSelectModule,
        MatTableModule,
        MatTooltipModule,
        NgxQRCodeModule,
    ],
  exports: [
  ],
  bootstrap: []
})
export class PaymenttypeModule {
}

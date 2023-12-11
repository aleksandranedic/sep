import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NotAuthorizedPageComponent} from './views/403/not-authorized-page/not-authorized-page.component';
import {NotFoundPageComponent} from './views/404/not-found-page/not-found-page.component';
import {HomepageContainerComponent} from './views/homepage/container/homepage-container/homepage-container.component';
import {
  DashboardContainerComponent
} from "./views/dashboard/container/dashboard-container/dashboard-container.component";
import {AuthGuard} from "./model/AuthGuard";
import {
  VerificationScreenContainerComponent
} from "./views/verification-screen/container/verification-screen-container/verification-screen-container.component";
import {PayCardComponent} from "./views/pay-card/pay-card.component";
import {PaymentSuccessComponent} from "./views/payment-success/payment-success.component";
import {PaymentFailedComponent} from "./views/payment-failed/payment-failed.component";
import {PaymentErrorComponent} from "./views/payment-error/payment-error.component";
import {PaymentSubscriptionComponent} from "./views/payment-subscription/payment-subscription.component";

const routes: Routes = [
  {path: '', component: HomepageContainerComponent},
  {
    path: 'dashboard',
    component: DashboardContainerComponent,
    canActivate: [AuthGuard],
    data: {roles: ['ROLE_PROPERTY_OWNER', 'ROLE_ADMIN']}
  },
  {path: 'registration/verification', component: VerificationScreenContainerComponent},
  {path: 'pay/card/:id', component: PayCardComponent},
  {path: 'payment/success', component: PaymentSuccessComponent},
  {path: 'payment/failed', component: PaymentFailedComponent},
  {path: 'payment/error', component: PaymentErrorComponent},
  {path: 'payment', component: PaymentSubscriptionComponent},
  {path: '403', component: NotAuthorizedPageComponent},
  {path: '**', component: NotFoundPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

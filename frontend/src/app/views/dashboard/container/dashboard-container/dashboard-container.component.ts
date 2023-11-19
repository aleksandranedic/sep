import {Component} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-dashboard-container',
  templateUrl: './dashboard-container.component.html',
  styleUrls: ['./dashboard-container.component.css']
})
export class DashboardContainerComponent {

  userRole: string

  constructor(public dialog: MatDialog) {
    this.userRole = localStorage.getItem("userRole") || ""
  }
}


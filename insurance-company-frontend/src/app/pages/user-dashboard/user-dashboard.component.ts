import {Component, OnInit} from '@angular/core';
import {AdminService} from '../../services/admin.service';
import {Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {Policy} from '../../models/policy-model';

@Component({
  selector: 'app-user-dashboard',
  standalone:true,
  imports: [
    FormsModule,
    NgIf,
    DecimalPipe,
    NgForOf,
    DatePipe
  ],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss'
})
export class UserDashboardComponent implements OnInit{
  selectedInsurance: any = null;
  startDate: string = '';
  endDate: string = '';
  insuranceTypes: any[] = [];
  today: string;
  purchasedPolicies: Policy[] = [];

  constructor(private adminService: AdminService, private router: Router) {
    this.today = this.formatDate(new Date());
  }

  ngOnInit() {
    this.loadInsuranceTypes();
    this.loadPurchasedPolicies();
  }

  onStartDateChange() {
    if (this.endDate && this.endDate < this.startDate!) {
      this.endDate = '';
    }
  }

  loadPurchasedPolicies() {
    this.adminService.getMyPolicies().subscribe({
      next: (data) => {
        this.purchasedPolicies = data;
      }
    });
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  isActive(endDate: string | Date, startDate: string | Date): boolean {
    const today = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);
    today.setHours(0, 0, 0, 0);
    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);
    return today >= start && today <= end;
  }

  loadInsuranceTypes() {
    this.adminService.getInsuranceTypes().subscribe(data => {
      this.insuranceTypes = data;
    });
  }

  openBuyDialog(insurance: any) {
    this.selectedInsurance = insurance;
    this.startDate = '';
    this.endDate = '';
  }

  buyInsurance() {
    const payload = {
      insurance_type_id: this.selectedInsurance.id,
      start_date: this.startDate,
      end_date: this.endDate
    };

    this.adminService.buyInsurance(payload).subscribe({
      next: (response : any) => {
        if (response && response.sessionUrl) {
          window.location.href = response.sessionUrl;
        } else {
          console.error('Stripe URL nije vraćen.');
        }
      },
      error: (err) => {
        console.error('Greška prilikom kupovine:', err);
      }
    });
  }

}

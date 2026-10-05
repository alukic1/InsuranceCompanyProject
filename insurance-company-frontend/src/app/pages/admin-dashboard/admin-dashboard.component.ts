import {Component, OnInit} from '@angular/core';
import {User} from '../../models/user.model';
import {InsuranceType} from '../../models/insurance-type.model';
import {AdminService} from '../../services/admin.service';
import {FormsModule} from '@angular/forms';
import {DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  standalone:true,
  imports: [
    NgIf,
    FormsModule,
    NgForOf,
    DecimalPipe,
    RouterLink
  ],
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  users: User[] = [];
  insuranceTypes: InsuranceType[] = [];

  selectedInsurance: InsuranceType | null = null;
  showInsuranceModal = false;

  showAdminModal = false;
  newAdmin = { username: '', email: '', nationalIdNumber: '', password: '',
    name: '', lastName:''
  };

  showAddInsuranceModal = false;
  newInsurance = { name: '', description: '', price: null };

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.adminService.getUsers().subscribe({
      next: users => this.users = users
    });
    this.adminService.getInsuranceTypes().subscribe({
      next: types => this.insuranceTypes = types
    });
  }

  toggleBlock(user: User) {

    this.adminService.toggleBlockUser(user.id).subscribe({
      next: (updatedUser: User) => {
        const index = this.users.findIndex(u => u.id === user.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
      }
    });
  }

  openAddAdminDialog() {
    this.showAdminModal = true;
  }

  addAdmin() {
    this.adminService.addAdmin(this.newAdmin).subscribe(() => {
      this.showAdminModal = false;
      this.newAdmin = { name: '', lastName:'', nationalIdNumber: '',username: '', email: '', password: '' };
      this.loadData();
    });
  }

  editInsurance(type: { name: string; description: string; id: number; price: number }) {
    this.selectedInsurance = { ...type };
    this.showInsuranceModal = true;
  }

  saveInsurance() {
    if (!this.selectedInsurance) return;

    const data = {
      name: this.selectedInsurance.name,
      description: this.selectedInsurance.description,
      price: this.selectedInsurance.price
    };

    this.adminService.updateInsurance(this.selectedInsurance.id, data).subscribe(() => {
      this.showInsuranceModal = false;
      this.selectedInsurance = null;
      this.loadData();
    });
  }

  openAddInsuranceDialog() {
    this.newInsurance = { name: '', description: '', price: null };
    this.showAddInsuranceModal = true;
  }

  addInsurance() {
    this.adminService.addInsurance(this.newInsurance).subscribe(() => {
      this.loadData();
      this.showAddInsuranceModal = false;
    });
  }

  cancelEdit() {
    this.selectedInsurance = null;
  }

  deleteInsurance(id: number) {
    if (confirm('Da li ste sigurni da želite obrisati ovu vrstu osiguranja?')) {
      this.adminService.deleteInsurance(id).subscribe(() => {
        this.loadData();
      });
    }
  }
}

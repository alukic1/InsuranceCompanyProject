import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {User} from '../models/user.model';
import {InsuranceType} from '../models/insurance-type.model';
import {Policy} from '../models/policy-model';
import {AuthService} from './auth.service';
import {Router} from '@angular/router';


@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = 'http://localhost:8080/api';


  constructor(private http: HttpClient, private auth: AuthService, private router: Router) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/users`);
  }

  getInsuranceTypes(): Observable<InsuranceType[]> {
    return this.http.get<InsuranceType[]>(`${this.apiUrl}/insurance-types`);
  }

  toggleBlockUser(userId: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${userId}/toggle-block`, {});
  }

  addAdmin(userData: any): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/users/add-admin`, userData);
  }

  updateInsurance(typeId: number, data: any): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/insurance-types/${typeId}`, data);
  }

  addInsurance(data: any) {
    return this.http.post<void>(`${this.apiUrl}/insurance-types`, data);
  }

  deleteInsurance(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/insurance-types/${id}`, {});
  }

  buyInsurance(data: any) {
    return this.http.post<void>(`${this.apiUrl}/stripe/checkout`, data).pipe(
      catchError(error => {

        if (error.status === 403) {
          alert("Vaš zahtjev je označen kao sumnjiv. Sesija je zaustavljena.");
          this.auth.logout();
          this.router.navigate(['/login']);
        }

        return throwError(() => error);
      })
    );
  }

  getMyPolicies() {
    return this.http.get<Policy[]>(`${this.apiUrl}/insurance-policies/my-policies`, {});
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'jwtToken';
  private roleKey = 'role';

  constructor(private http: HttpClient) {}

  register(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/authenticate`, data);
  }

  verifyCode(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/verify-code`, data);
  }

  saveToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  saveRole(role: string) {
    localStorage.setItem(this.roleKey, role);
  }

  getRole(): string | null{
    return localStorage.getItem(this.roleKey);
  }

  getUsername(): string | null {
    const token = this.getToken();
    if (!token) return null;

    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload?.sub || null;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();

    if (!token) {
      return false;
    }

    const payload = this.decodeToken(token);

    if (!payload) {
      return false;
    }

    const expiry = payload.exp;


    const isExpired = (Date.now() / 1000) > expiry;

    return !isExpired;
  }

  private decodeToken(token: string): any {
    try {
      const payloadPart = token.split('.')[1];
      const decoded = atob(payloadPart);
      return JSON.parse(decoded);
    } catch (e) {
      return null;
    }
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
  }
}

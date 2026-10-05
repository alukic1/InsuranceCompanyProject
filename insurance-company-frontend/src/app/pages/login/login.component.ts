import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {CommonModule} from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-login',
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  form: FormGroup;
  error: string = '';

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  submit() {
    if (this.form.invalid) { this.error = "Sva polja su obavezna";
    return;
    }
    this.auth.login(this.form.value).subscribe({
      next: (res) => {
        if (res.twoFactorRequired) {
          this.router.navigate(['/verify-code'], { queryParams: { username: this.form.value.username } });
        }
      },
      error: (err) => {
        if (err.status === 423) {
          alert("Vaš nalog je blokiran. Molimo kontaktirajte administratora.");

        }
        if (err.status === 401) {
          this.error = 'Pogrešno korisničko ime ili lozinka.';
        }

      }
    });
  }
}

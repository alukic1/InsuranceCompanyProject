import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [
    NgIf,
    RouterLink,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  form: FormGroup;
  error: string = '';

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      nationalIdNumber: ['', Validators.required]
    });
  }

  submit() {
    if (this.form.invalid) { this.error = "Sva polja su obavezna";
      return;
    }
    this.auth.register(this.form.value).subscribe({
    next: (res) => {
      console.log(res);
      this.router.navigate(['/login']);

    },
      error: (err) => {
        if (err.status === 401) {
          this.error = 'Zauzeto korisničko ime ili email.';
        }

      }
    });
  }
}

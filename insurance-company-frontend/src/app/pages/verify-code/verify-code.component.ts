import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-verify-code',
  imports: [
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './verify-code.component.html'
})
export class VerifyCodeComponent {
  form: FormGroup;
  error: string = '';
  username = '';

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private auth: AuthService, private router: Router) {
    this.route.queryParams.subscribe(params => this.username = params['username']);
    this.form = this.fb.group({
      code: ['', Validators.required]
    });
  }



  submit() {
    console.log('Klik na submit');
    console.log('Form vrijednosti: ', this.form.value);
    console.log('Username iz URL: ', this.username);
    if (this.form.invalid) {
      this.error = "Sva polja su obavezna";
      return;
    }

    const body = {
      username: this.username,
      code: this.form.value.code
    };

    this.auth.verifyCode(body).subscribe({
      next: (res) => {
        console.log(res);
        this.auth.saveToken(res.token);
        this.auth.saveRole(res.role);


        if (res.role === 'ROLE_ADMIN') {
          this.auth.saveRole('ADMIN');
          this.router.navigate(['/admin-dashboard']);
        } else {
          this.auth.saveRole('USER');
          this.router.navigate(['/user-dashboard']);
        }
      },
      error: (err) => {

        if (err.status === 423) {
          alert("Previše neuspješnih pokušaja. Vaš nalog je blokiran. Molimo kontaktirajte administratora.");
          this.router.navigate(['/'])
        }
        if (err.status === 401) {
          this.error = 'Kod je pogrešan ili je istekao.';
        }
      }
    });
  }
}

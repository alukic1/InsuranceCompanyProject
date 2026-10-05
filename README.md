# InsuranceCompanyProject

Web application (backend + frontend) with Stripe payments, Graylog log monitoring and Nagios server monitoring, built as a university project at the University of Banja Luka, Faculty of Electrical Engineering.

See the full project description: [Project_description.pdf](Project_description.pdf)

## Configuration

Real config files are not included because they contain secrets.
Copy `application.properties.example` to `application.properties` and fill in
your own values.

## Running

1. Start Graylog: `docker compose up -d`, then open `http://localhost:9000`
2. Start the backend and frontend applications
3. Forward Stripe webhooks: `stripe listen --forward-to localhost:8080/api/stripe/webhook`
4. Open `http://localhost/nagios` in the browser inside the VM

## Not included

`application.properties` and other secrets are excluded via `.gitignore`.
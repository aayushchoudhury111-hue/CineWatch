# Afterglow

Afterglow is a cinema discovery and ticket booking app built with Spring Boot. The responsive frontend is served by the same application as the REST API.

## Run locally

Install the JDK version configured in `pom.xml`, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Sign in or create an account to continue. The app reads movies, theatres, screens, seats, and shows from MySQL. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in your environment for your existing database before starting; credentials and local database files are excluded from Git.

## Included flows

- Browse and search movies, filter by genre, and choose a screening date.
- View showtimes, select available seats, and book up to eight seats.
- Read movie and city listings directly from the configured database; no movie or theatre sample data is inserted. Missing show-seat rows are created from seats already attached to the screen.
- Register or sign in with a password stored as a salted PBKDF2 hash; sessions protect booking routes.
- Find bookings from your account or with your booking reference, and cancel a confirmed booking.
- Manage movies through the `/api/movies` REST endpoints.

Checkout records a simulated payment for this project demo. Connect a payment provider before accepting real payments.

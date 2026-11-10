package com.example.bookmyshow.Controller;

import com.example.bookmyshow.DTO.BookingDto;
import com.example.bookmyshow.DTO.BookingRequestDto;
import com.example.bookmyshow.Service.BookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingRequestDto bookingRequest, HttpSession session) {
        bookingRequest.setUserId(requireUserId(session));
        return new ResponseEntity<>(bookingService.createBooking(bookingRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<BookingDto> getBookingNumber(@Valid @RequestParam String bookingNumber, HttpSession session) {
        return ResponseEntity.ok(owned(bookingService.getBookingByNumber(bookingNumber), session));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable long id, HttpSession session) {
        return ResponseEntity.ok(owned(bookingService.getBookingById(id), session));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getBookingsByUser(@PathVariable Long userId, HttpSession session) {
        if (!requireUserId(session).equals(userId)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable Long id, HttpSession session) {
        owned(bookingService.getBookingById(id), session);
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    private Long requireUserId(HttpSession session) {
        Object id = session.getAttribute(com.example.bookmyshow.Controller.AuthController.USER_ID);
        if (!(id instanceof Long userId)) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userId;
    }

    private BookingDto owned(BookingDto booking, HttpSession session) {
        if (!booking.getUser().getId().equals(requireUserId(session))) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        return booking;
    }



}

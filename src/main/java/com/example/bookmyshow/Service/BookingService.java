package com.example.bookmyshow.Service;

import com.example.bookmyshow.DTO.*;
import com.example.bookmyshow.Entity.*;
import com.example.bookmyshow.Exception.ResourceNotFoundException;
import com.example.bookmyshow.Exception.SeatUnavailableException;
import com.example.bookmyshow.Repository.BookingRepository;
import com.example.bookmyshow.Repository.ShowRepository;
import com.example.bookmyshow.Repository.ShowSeatRepository;
import com.example.bookmyshow.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    ShowSeatRepository showSeatRepository;

    @Transactional

    public BookingDto createBooking(BookingRequestDto bookingRequest) {
        Users users = userRepository.findById(bookingRequest.getUserId()).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Show show = showRepository.findById(bookingRequest.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        if (bookingRequest.getSeatIds() == null || bookingRequest.getSeatIds().isEmpty()) {
            throw new SeatUnavailableException("Choose one or more valid seats for this show");
        }
        List<ShowSeat> selectedSeats = showSeatRepository.findAllForUpdateByIdIn(bookingRequest.getSeatIds().stream().distinct().toList());
        if (selectedSeats.size() != bookingRequest.getSeatIds().stream().distinct().count()) {
            throw new SeatUnavailableException("A selected seat could not be found");
        }

        for(ShowSeat seat : selectedSeats) {
            if(!"AVAILABLE".equals(seat.getStatus())) {
                throw new SeatUnavailableException("Seat "+seat.getSeat().getSeatNumber() + "is not available");
            }
            if (!seat.getShow().getId().equals(show.getId())) {
                throw new SeatUnavailableException("A selected seat does not belong to this show");
            }
            seat.setStatus("LOCKED");
        }
        showSeatRepository.saveAll(selectedSeats);

        Double totalAmount = selectedSeats.stream().
                mapToDouble(seat -> seat.getPrice()).sum();

        Payment payment = new Payment();
        payment.setAmount(totalAmount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMethod(bookingRequest.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setTransactionId(UUID.randomUUID().toString());

        // booking confirm karenge

        Booking booking = new Booking();
        booking.setUser(users);
        booking.setShow(show);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setTotalAmount(totalAmount);
        booking.setBookingNumber(UUID.randomUUID().toString());
        booking.setPayment(payment);

        Booking saveBooking = bookingRepository.save(booking);

        selectedSeats.forEach(seat -> {
            seat.setStatus("BOOKED");
            seat.setBooking(saveBooking);
        });
        showSeatRepository.saveAll(selectedSeats);

        return mapToBookingDto(saveBooking, selectedSeats);
    }

    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Booking Not Found"));
        List<ShowSeat> seats = showSeatRepository.findAll()
                .stream()
                .filter(seat -> seat.getBooking()!=null && seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());
        return mapToBookingDto(booking, seats);
    }

    public BookingDto getBookingByNumber(String bookingNumber) {
        Booking booking = bookingRepository.findByBookingNumber(bookingNumber).
                orElseThrow(() -> new ResourceNotFoundException("Booking Not Found"));
        List<ShowSeat> seats = showSeatRepository.findAll()
                .stream()
                .filter(seat -> seat.getBooking()!=null && seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());
        return mapToBookingDto(booking, seats);
    }

    public List<BookingDto> getBookingByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserUserId(userId);
        return bookings.stream()
                .map(booking -> {
                    List<ShowSeat> seats = showSeatRepository.findAll()
                            .stream()
                            .filter(seat -> seat.getBooking()!=null && seat.getBooking().getId().equals(booking.getId()))
                            .collect(Collectors.toList());
                    return mapToBookingDto(booking, seats);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto cancelBooking(Long id) { return canceBooking(id); }

    @Transactional
    public BookingDto canceBooking(Long id){
        Booking booking = bookingRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Booking Not Found"));
        if ("CANCELED".equals(booking.getStatus())) {
            List<ShowSeat> seats = showSeatRepository.findByShow_Id(booking.getShow().getId()).stream()
                    .filter(seat -> seat.getBooking() != null && seat.getBooking().getId().equals(booking.getId())).toList();
            return mapToBookingDto(booking, seats);
        }
        booking.setStatus("CANCELED");
        List<ShowSeat> seats = showSeatRepository.findAll()
                .stream()
                .filter(seat -> seat.getBooking()!=null && seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());

        seats.forEach(seat -> {
            seat.setStatus("AVAILABLE");
            seat.setBooking(null);
        });
        if(booking.getPayment()!=null) {
            booking.getPayment().setStatus("REFUNDED");
        }
        Booking updateBooking = bookingRepository.save(booking);
        showSeatRepository.saveAll(seats);
        return mapToBookingDto(updateBooking, seats);
    }

    private BookingDto mapToBookingDto(Booking booking, List<ShowSeat> seats) {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setBookingNumber(booking.getBookingNumber());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setTotalAmount(booking.getTotalAmount());
        bookingDto.setId(booking.getId());
        bookingDto.setStatus(booking.getStatus());

        // User
        UserDto userDto = new  UserDto();
        userDto.setId(booking.getUser().getUserId());
        userDto.setEmail(booking.getUser().getEmail());
        userDto.setName(booking.getUser().getName());
        userDto.setPhoneNumber(booking.getUser().getPhoneNumber());
        bookingDto.setUser(userDto);

        ShowDto showDto = new ShowDto();
        showDto.setId(booking.getShow().getId());
        showDto.setStartTime(booking.getShow().getStartTime());
        showDto.setEndTime(booking.getShow().getEndTime());


        MovieDto movieDto = new MovieDto();
        movieDto.setId(booking.getShow().getMovie().getId());
        movieDto.setTitle(booking.getShow().getMovie().getTitle());
        movieDto.setDescription(booking.getShow().getMovie().getDescription());
        movieDto.setLanguage(booking.getShow().getMovie().getLanguage());
        movieDto.setGenre(booking.getShow().getMovie().getGenre());
        movieDto.setDurationMins(booking.getShow().getMovie().getDuration());
        movieDto.setReleaseDate(booking.getShow().getMovie().getReleaseDate());
        movieDto.setPosterUrl(booking.getShow().getMovie().getPosterUrl());
        showDto.setMovie(movieDto);

        ScreenDto screenDto = new ScreenDto();
        screenDto.setId(booking.getShow().getScreen().getScreenId());
        screenDto.setName(booking.getShow().getScreen().getName());
        screenDto.setTotalSeats(booking.getShow().getScreen().getTotalSeats());

        TheatreDto theatreDto = new TheatreDto();
        theatreDto.setId(booking.getShow().getScreen().getTheatre().getId());
        theatreDto.setName(booking.getShow().getScreen().getTheatre().getName());
        theatreDto.setAddress(booking.getShow().getScreen().getTheatre().getAddress());
        theatreDto.setCity(booking.getShow().getScreen().getTheatre().getCity());
        theatreDto.setTotalScreens(booking.getShow().getScreen().getTheatre().getTotalScreen());

        screenDto.setTheatre(theatreDto);

        showDto.setScreen(screenDto);
        bookingDto.setShow(showDto);

        List<ShowSeatDto> seatDtos = seats.stream()
                .map(seat-> {
                    ShowSeatDto seatDto = new ShowSeatDto();
                    seatDto.setId(seat.getId());
                    seatDto.setStatus(seat.getStatus());
                    seatDto.setPrice(seat.getPrice());

                    SeatDto baseSeatDto = new SeatDto();
                    baseSeatDto.setId(seat.getSeat().getId());
                    baseSeatDto.setSeatNumber(seat.getSeat().getSeatNumber());
                    baseSeatDto.setSeatType(seat.getSeat().getSeatType());
                    seatDto.setSeat(baseSeatDto);
                    return seatDto;

                })
                .collect(Collectors.toList());

        bookingDto.setSeats(seatDtos);

        if(booking.getPayment() != null) {
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setId(booking.getPayment().getPaymentId());
            paymentDto.setAmount(booking.getPayment().getAmount());
            paymentDto.setPaymentMethod(booking.getPayment().getPaymentMethod());
            paymentDto.setTransactionId(booking.getPayment().getTransactionId());
            paymentDto.setStatus(booking.getPayment().getStatus());
            paymentDto.setPaymentTime(booking.getPayment().getPaymentTime());
            bookingDto.setPayment(paymentDto);
        }
        return bookingDto;
    }
}

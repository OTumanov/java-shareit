//package ru.practicum.shareit.booking.dto;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//import ru.practicum.shareit.validation.Create;
//
//import javax.validation.constraints.FutureOrPresent;
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//public class BookingInItemDto {
//    private Long id;
//    private Long bookerId;
//    @FutureOrPresent(groups = {Create.class})
//    private LocalDateTime start;
//    @FutureOrPresent(groups = {Create.class})
//    private LocalDateTime end;
//}
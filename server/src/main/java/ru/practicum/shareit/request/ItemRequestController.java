//package ru.practicum.shareit.request;
//
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import ru.practicum.shareit.request.dto.RequestDto;
//import ru.practicum.shareit.request.service.ItemRequestService;
//
//import java.util.List;
//
//
//@RestController
//@RequestMapping(path = "/requests")
//@AllArgsConstructor
//public class ItemRequestController {
//
//    private final ItemRequestService service;
//
//    @PostMapping
//    public RequestDto createRequest(@RequestBody RequestDto requestDto,
//                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
//        return service.createRequest(requestDto, userId);
//    }
//
//    @GetMapping
//    public List<RequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
//        return service.findAllByUserId(userId);
//    }
//
//    @GetMapping("/all")
//    public List<RequestDto> findAll(@RequestParam(defaultValue = "0") int from,
//                                    @RequestParam(defaultValue = "20") int size,
//                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
//        return service.findAll(from, size, userId);
//    }
//
//    @GetMapping("/{requestId}")
//    public RequestDto findById(@PathVariable Long requestId,
//                               @RequestHeader("X-Sharer-User-Id") Long userId) {
//        return service.findById(requestId, userId);
//    }
//}

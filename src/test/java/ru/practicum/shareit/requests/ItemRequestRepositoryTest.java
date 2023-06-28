package ru.practicum.shareit.requests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storge.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private RequestsRepository requestsRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User requestor;
    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        LocalDateTime dateTime = LocalDateTime.now();
        user = userRepository.save(new User(null, "user", "user@email.com"));
        requestor = userRepository.save(new User(null, "requestor", "requestor@email.com"));
        itemRequest = requestsRepository.save(new ItemRequest(null, "request", requestor.getId(), dateTime, new ArrayList<>()));
    }

    @Test
    public void findRequestByRequestorOrderByCreatedDescTest() {
        List<ItemRequest> result = requestsRepository.findItemRequestByRequesterOrderByCreatedDesc(requestor.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
        assertEquals(itemRequest.getRequester(), result.get(0).getRequester());
        assertEquals(itemRequest.getCreated(), result.get(0).getCreated());
    }

    @Test
    public void findAllTest() {
        Page<ItemRequest> result = requestsRepository.findAll(user.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemRequest.getDescription(), result.getContent().get(0).getDescription());
        assertEquals(itemRequest.getRequester(), result.getContent().get(0).getRequester());
        assertEquals(itemRequest.getCreated(), result.getContent().get(0).getCreated());
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        requestsRepository.deleteAll();
    }
}
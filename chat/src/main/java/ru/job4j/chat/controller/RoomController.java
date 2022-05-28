package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.RoomRepository;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RestTemplate rest;

    private static final String API = "http://localhost:8080/message/";

    private static final String API_ID = "http://localhost:8080/message/{id}";

    private final RoomRepository rr;

    public RoomController(final RoomRepository rr) {
        this.rr = rr;
    }

    @GetMapping("/")
    public List<Room> findAll() {
        return (List<Room>) this.rr.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        Message rsl = rest.postForObject(API, message, Message.class);
        return new ResponseEntity<>(
                rsl,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}")
    public ResponseEntity<Message> create(@RequestBody Message message, @PathVariable int id) {
        var em = this.rr.findById(id);
        Message rsl = rest.postForObject(API, message, Message.class);
        if (em.isPresent()) {
            Room e = em.get();
            e.getMessages().add(rsl);
            this.rr.save(e);
        }
        return new ResponseEntity<>(
                rsl,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        rest.put(API, message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/{idE}")
    public ResponseEntity<Void> delete(@PathVariable int id, @PathVariable int idE) {
        var em = this.rr.findById(idE);
        Message message = new Message();
        message.setId(id);
        if (em.isPresent()) {
            Room e = em.get();
            e.getMessages().remove(message);
            this.rr.save(e);
        }
        rest.delete(API_ID, id);
        return ResponseEntity.ok().build();
    }
}

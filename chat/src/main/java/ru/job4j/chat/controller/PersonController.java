package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.repository.RoomRepository;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private RestTemplate rest;

    private static final String API = "http://localhost:8080/message/";

    private static final String API_ID = "http://localhost:8080/message/{id}";

    private final PersonRepository pr;

    public PersonController(final PersonRepository pr) {
        this.pr = pr;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return (List<Person>) this.pr.findAll();
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
        var em = this.pr.findById(id);
        Message rsl = rest.postForObject(API, message, Message.class);
        if (em.isPresent()) {
            Person e = em.get();
            e.getMessages().add(rsl);
            this.pr.save(e);
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
        var em = this.pr.findById(idE);
        Message message = new Message();
        message.setId(id);
        if (em.isPresent()) {
            Person e = em.get();
            e.getMessages().remove(message);
            this.pr.save(e);
        }
        rest.delete(API_ID, id);
        return ResponseEntity.ok().build();
    }
}

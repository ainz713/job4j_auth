package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Role;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.RoomRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    @PatchMapping("/example2")
    public Optional<Room> example2(@RequestBody Room room) throws InvocationTargetException,
            IllegalAccessException {
        var current = rr.findById(room.getId());
        var methods = current.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method: methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Impossible invoke set method from object : " + current + ", Check set and get pairs.");
                }
                var newValue = getMethod.invoke(room);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        rr.save(room);
        return current;
    }
}

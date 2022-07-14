package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.repository.PersonRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());

    private final ObjectMapper objectMapper;

    @Autowired
    private RestTemplate rest;

    private static final String API = "http://localhost:8080/message/";

    private static final String API_ID = "http://localhost:8080/message/{id}";

    private final PersonRepository pr;

    public PersonController(ObjectMapper objectMapper, final PersonRepository pr) {
        this.objectMapper = objectMapper;
        this.pr = pr;
    }

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> rsl = (List<Person>) this.pr.findAll();
        return new ResponseEntity<>(
                rsl,
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        if (id > 5) {
            throw new IllegalArgumentException(
                    "Invalid id. Id must be < 5.");
        }
        var person = this.pr.findById(id);
        return new ResponseEntity<>(
                person.orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check id.")),
                HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@Valid @RequestBody Message message) {
        Message rsl = rest.postForObject(API, message, Message.class);
        return new ResponseEntity<>(
                rsl,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}")
    public ResponseEntity<Message> create(@Valid @RequestBody Message message, @PathVariable int id) {
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
    public ResponseEntity<Void> update(@Valid @RequestBody Message message) {
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

    @PatchMapping("/example2")
    public Optional<Person> example2(@Valid @RequestBody Person person) throws InvocationTargetException,
            IllegalAccessException {
        var current = pr.findById(person.getId());
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
                var newValue = getMethod.invoke(person);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        pr.save(person);
        return current;
    }

    @ExceptionHandler(value = {NumberFormatException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}

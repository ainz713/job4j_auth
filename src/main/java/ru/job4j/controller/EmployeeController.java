package ru.job4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.domain.Employee;
import ru.job4j.domain.Person;
import ru.job4j.repository.EmployeeRepository;
import ru.job4j.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private RestTemplate rest;

    private static final String API = "http://localhost:8080/person/";

    private static final String API_ID = "http://localhost:8080/person/{id}";

    private final EmployeeRepository es;

    public EmployeeController(final EmployeeRepository es) {
        this.es = es;
    }

    @GetMapping("/")
    public List<Employee> findAll() {
        return (List<Employee>) this.es.findAll();
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        Person rsl = rest.postForObject(API, person, Person.class);
        return new ResponseEntity<>(
                rsl,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}")
    public ResponseEntity<Person> create(@RequestBody Person person, @PathVariable int id) {
        var em = this.es.findById(id);
        Person rsl = rest.postForObject(API, person, Person.class);
        if (em.isPresent()) {
            Employee e = em.get();
            e.getAccounts().add(rsl);
            this.es.save(e);
        }
        return new ResponseEntity<>(
                rsl,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        rest.put(API, person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/{idE}")
    public ResponseEntity<Void> delete(@PathVariable int id, @PathVariable int idE) {
        var em = this.es.findById(idE);
        Person person = new Person();
        person.setId(id);
        if (em.isPresent()) {
            Employee e = em.get();
            e.getAccounts().remove(person);
            this.es.save(e);
        }
        rest.delete(API_ID, id);
        return ResponseEntity.ok().build();
    }
}

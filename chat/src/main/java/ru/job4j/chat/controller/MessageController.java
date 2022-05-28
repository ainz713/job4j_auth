package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.repository.MessageRepository;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(final MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return (List<Message>) this.messageRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var person = this.messageRepository.findById(id);
        return new ResponseEntity<Message>(
                person.orElse(new Message()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        return new ResponseEntity<Message>(
                this.messageRepository.save(message),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        this.messageRepository.save(message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Message message = new Message();
        message.setId(id);
        this.messageRepository.delete(message);
        return ResponseEntity.ok().build();
    }
}

package ru.job4j.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.chat.domain.Message;

public interface MessageRepository extends CrudRepository<Message, Integer> {
}

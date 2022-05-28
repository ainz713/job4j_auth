package ru.job4j.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.chat.domain.Room;

public interface RoomRepository extends CrudRepository<Room, Integer> {
}

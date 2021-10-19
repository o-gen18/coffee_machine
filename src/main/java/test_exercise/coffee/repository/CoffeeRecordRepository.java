package test_exercise.coffee.repository;

import org.springframework.data.repository.CrudRepository;
import test_exercise.coffee.model.CoffeeRecord;

import java.util.List;

public interface CoffeeRecordRepository extends CrudRepository<CoffeeRecord, Long> {
    List<CoffeeRecord> findAllByOrderByEventTimeDesc();
}
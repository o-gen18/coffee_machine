package test_exercise.coffee.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a single record of the coffee machine's work.
 */
@Entity
@Table(name = "records")
public class CoffeeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @JsonFormat(timezone = "Europe/Moscow")
    private Timestamp eventTime;

    @Convert(converter = CoffeeType.CoffeeTypeConverter.class)
    private CoffeeType coffeeType;

    private String event;

    public static CoffeeRecord of(String event, CoffeeType coffeeType) {
        CoffeeRecord coffeeRecord = new CoffeeRecord();
        coffeeRecord.event = event;
        coffeeRecord.coffeeType = coffeeType;
        coffeeRecord.eventTime = Timestamp.from(Instant.now());
        return coffeeRecord;
    }

    public CoffeeRecord() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public CoffeeType getCoffeeType() {
        return coffeeType;
    }

    public void setCoffeeType(CoffeeType coffeeType) {
        this.coffeeType = coffeeType;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeRecord coffeeRecord = (CoffeeRecord) o;
        return id == coffeeRecord.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

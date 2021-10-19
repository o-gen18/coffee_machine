package test_exercise.coffee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test_exercise.coffee.model.CoffeeRecord;
import test_exercise.coffee.repository.CoffeeRecordRepository;
import test_exercise.coffee.service.CoffeeMachine;
import test_exercise.coffee.service.CoffeeMachineImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coffee")
public class CoffeeMachineController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeMachineController.class.getSimpleName());
    private final CoffeeMachine coffeeMachine;
    private final ObjectMapper objectMapper;
    private final CoffeeRecordRepository coffeeRecordRepository;

    public CoffeeMachineController(CoffeeMachine coffeeMachine,
                                   ObjectMapper objectMapper,
                                   CoffeeRecordRepository coffeeRecordRepository) {
        this.coffeeMachine = coffeeMachine;
        this.objectMapper = objectMapper;
        this.coffeeRecordRepository = coffeeRecordRepository;
    }

    /**
     * Starts the coffee preparation if the machine is not busy.
     * @param coffeeType The type of coffee to prepare.
     * @return Response with
     */
    @GetMapping("/{coffeeType}")
    public ResponseEntity<CoffeeRecord> getCoffee(@PathVariable(name = "coffeeType") String coffeeType) {
        return new ResponseEntity<>(coffeeMachine.serve(coffeeType), HttpStatus.OK);
    }

    /**
     * Shows all the work track of the coffee machine.
     * @return Response with list of records.
     */
    @GetMapping("/records")
    public ResponseEntity<List<CoffeeRecord>> showRecords() {
        List<CoffeeRecord> records = coffeeRecordRepository.findAllByOrderByEventTimeDesc();
        if (records.isEmpty()) {
           return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(records, HttpStatus.OK);
        }
    }

    /**
     * Cleans up the history of coffee machine's work.
     * @return Response with ok status.
     */
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteRecords() {
        coffeeRecordRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Handles the exceptions that may happen during coffee machine work.
     * Sends the exception to the client.
     * @param e Exception that occurred.
     * @param response Response object.
     * @throws IOException Signals that I/O exception has occurred.
     */
    @ExceptionHandler(value = {IllegalStateException.class, UnsupportedOperationException.class})
    public void handleException(Exception e, HttpServletResponse response)
        throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "message", e.getMessage(),
                "type", e.getClass())
        ));
        LOGGER.error(e.getMessage());
    }
}

package test_exercise.coffee.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import test_exercise.coffee.model.CoffeeRecord;
import test_exercise.coffee.model.CoffeeType;
import test_exercise.coffee.repository.CoffeeRecordRepository;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Imitates the work of coffee machine. The machine can serve only one client at a time.
 * This is backed by atomic boolean variable that holds whether or not the machine is working.
 * While coffee machine is working it is impossible to order another coffee.
 * Marked as a service to be singleton.
 * Has private constructor to prohibit instantiation.
 */
@Service
public class CoffeeMachineImpl implements CoffeeMachine {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeMachineImpl.class.getSimpleName());

    private final CoffeeRecordRepository coffeeRecordRepository;

    /**
     * Indicates whether the machine is busy at a given moment.
     */
    private AtomicBoolean isBusy = new AtomicBoolean(false);

    private CoffeeMachineImpl(CoffeeRecordRepository coffeeRecordRepository) {
        this.coffeeRecordRepository = coffeeRecordRepository;
    }

    /**
     * Starts the coffee machine.
     * @return true if the operation succeeded, otherwise false.
     */
    @Override
    public boolean start() {
        return isBusy.compareAndSet(false, true);
    }

    /**
     * Stops the coffee machine.
     * @return true if the operation succeeded, otherwise false.
     */
    @Override
    public boolean stop() {
        return isBusy.compareAndSet(true, false);
    }

    /**
     * Imitates the working process of the coffee machine.
     * @param coffee String representation of coffee type being ordered.
     * @return coffee record instance storing information about the order.
     */
    @Override
    public CoffeeRecord serve(String coffee) {
        CoffeeType coffeeType;
        try {
            coffeeType = CoffeeType.fromString(coffee);
        } catch (UnsupportedOperationException e) {
            coffeeRecordRepository.save(CoffeeRecord.of(
                    "Error occurred while trying to order a coffee because: " + e.getMessage(),
                    null
            ));
            throw e;
        }

        if (!start()) {
            coffeeRecordRepository.save(CoffeeRecord.of(
                    "Unsuccessful attempt to order a coffee. The machine is busy.", coffeeType));
            throw new IllegalStateException("Sorry, the coffee machine is now busy.");
        }

        CoffeeRecord responseRecord = CoffeeRecord.of(
                "A coffee has been ordered. Will be done in " + coffeeType.getPreparationTime() + " seconds.", coffeeType);
        new Thread(() -> {
            try {
                coffeeRecordRepository.save(responseRecord);
                Thread.sleep(coffeeType.getPreparationTime() * 1000);
                coffeeRecordRepository.save(CoffeeRecord.of("Serving finished.", coffeeType));
            } catch (InterruptedException e) {
                LOGGER.error("Error occurred while serving the coffee", e);
            } finally {
                stop();
            }
        }).start();
        return responseRecord;
    }
}

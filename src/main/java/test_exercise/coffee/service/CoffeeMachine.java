package test_exercise.coffee.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import test_exercise.coffee.model.CoffeeType;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CoffeeMachine {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeMachine.class.getSimpleName());

    private AtomicBoolean isInService = new AtomicBoolean(false);

    private static final CoffeeMachine coffeeMachine = new CoffeeMachine();

    public static CoffeeMachine getInstance() {
        return coffeeMachine;
    }

    private CoffeeMachine() {
    }

    public boolean isInService() {
        return isInService.get();
    }

    public boolean start() {
        return isInService.compareAndSet(false, true);
    }

    public boolean stop() {
        return isInService.compareAndSet(true, false);
    }

    public void serve(CoffeeType coffeeType) {
        LOGGER.info("Starting preparing " + coffeeType.toString()
                + ". Please, wait for " + coffeeType.getPreparationTime() + " seconds.");
        try {
            Thread.sleep(coffeeType.getPreparationTime() * 1000);
        } catch (InterruptedException e) {
            LOGGER.error("Error occurred while serving the coffee ", e);
        }
    }
}

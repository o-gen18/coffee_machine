package test_exercise.coffee.service;

import test_exercise.coffee.model.CoffeeRecord;

public interface CoffeeMachine {

    boolean isInService();

    boolean start();

    boolean stop();

    CoffeeRecord serve(String coffee);
}

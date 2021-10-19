package test_exercise.coffee.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.AttributeConverter;

public enum CoffeeType {
    AMERICANO("Americano", 10),
    CAPPUCCINO("Cappuccino", 15),
    ESPRESSO("Espresso", 5),
    LATTE("Latte", 25),
    WITH_MILK("With milk", 20);

    /**
     * Name of the coffee type.
     */
    private final String coffeeType;

    /**
     * Time of preparation, seconds.
     */
    private final int preparationTime;

    CoffeeType(String coffeeType, int preparationTime) {
        this.coffeeType = coffeeType;
        this.preparationTime = preparationTime;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    /**
     * Utility method for both Jackson and AttributeConverter
     * of Hibernate to allow them convert the string representation of CoffeeType into
     * enum.
     *
     * @param coffeeType The type of coffee.
     * @return Coffee type enum representation.
     */
    @JsonCreator
    public static CoffeeType fromString(String coffeeType) {
        return switch (coffeeType) {
            case "Americano" -> AMERICANO;
            case "Cappuccino" -> CAPPUCCINO;
            case "Espresso" -> ESPRESSO;
            case "Latte" -> LATTE;
            case "With milk" -> WITH_MILK;
            default -> throw new UnsupportedOperationException(
                    "The submitted coffee type " + coffeeType + " is not supported!"
            );
        };
    }

    /**
     * Utility method for both Jackson and AttributeConverter
     * of Hibernate to allow them convert the enum representation of CoffeeType into
     * the string.
     * @return CoffeeType's String representation.
     */
    @JsonValue
    public String getCoffeeType() {
        return coffeeType;
    }

    /**
     * Converts enum into string when saving into the database
     * and visa versa when retrieving.
     */
    public static class CoffeeTypeConverter
            implements AttributeConverter<CoffeeType, String> {

        @Override
        public String convertToDatabaseColumn(CoffeeType coffeeType) {
            return coffeeType == null? null : coffeeType.getCoffeeType();
        }

        @Override
        public CoffeeType convertToEntityAttribute(String s) {
            return s == null? null : fromString(s);
        }
    }
}

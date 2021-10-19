package test_exercise.coffee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import test_exercise.coffee.controller.CoffeeMachineController;
import test_exercise.coffee.service.CoffeeMachineImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
/*
 Using hsqldb library for in memory imitation of the database work.
 */
@TestPropertySource(locations = "classpath:test-app.properties")
/*
 Because the coffee machine is singleton, have to restart the context before each test case
 to have the initial state of coffee machine and to clean up the "in memory" database.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CoffeeApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CoffeeMachineImpl coffeeMachine;

    @Autowired
    private CoffeeMachineController coffeeMachineController;

    @Test
    void whenOrderAmericanoThenOK() throws Exception {
        this.mockMvc.perform(get("/coffee/Americano"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("coffeeType").value("Americano"))
        .andExpect(jsonPath("event").value("A coffee has been ordered. Will be done in 10 seconds."));
    }

    @Test
    void whenOrderTwoInARowThenBusy() throws Exception {
        this.mockMvc.perform(get("/coffee/Americano"));
        this.mockMvc.perform(get("/coffee/Latte"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("type").value("java.lang.IllegalStateException"))
        .andExpect(jsonPath("message").value("Sorry, the coffee machine is now busy."));
    }

    @Test
    void whenWrongNameThenError() throws Exception {
        this.mockMvc.perform(get("/coffee/wrongName"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("type").value("java.lang.UnsupportedOperationException"))
                .andExpect(jsonPath("message").value("The submitted coffee type wrongName is not supported!"));
    }

    @Test
    void whenShowRecordsThenReturnList() throws Exception {
        this.mockMvc.perform(get("/coffee/Espresso"));
        this.mockMvc.perform(get("/coffee/Americano"));
        Thread.sleep(6000); //Ensure the machine has finished preparing espresso which takes 5 seconds.
        this.mockMvc.perform(get("/coffee/records"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].event").value("A coffee has been ordered. Will be done in 5 seconds."))
                .andExpect(jsonPath("$[2].coffeeType").value("Espresso"))
                .andExpect(jsonPath("$[1].event").value("Unsuccessful attempt to order a coffee. The machine is busy."))
                .andExpect(jsonPath("$[1].coffeeType").value("Americano"))
                .andExpect(jsonPath("$[0].event").value("Serving finished."))
                .andExpect(jsonPath("$[0].coffeeType").value("Espresso"));
    }
}

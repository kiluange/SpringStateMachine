package com.example.statemachine;

import com.example.statemachine.events.OrderEvents;
import com.example.statemachine.states.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

import java.time.DayOfWeek;
import java.time.LocalDate;

@SpringBootApplication
public class StatemachineApplication implements CommandLineRunner {

    @Autowired
    private StateMachine<OrderStates, OrderEvents> stateMachine;

    public static void main(String[] args) {
        SpringApplication.run(StatemachineApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Iniciando máquina de estados...");
        stateMachine.sendEvent(OrderEvents.CONFIRMED_PAYMENT);
        stateMachine.sendEvent(OrderEvents.INVOICE_ISSUED);


        Message<OrderEvents> orderEventsMessage = MessageBuilder
                .withPayload(OrderEvents.SHIP)
                .setHeader("orderNumber", "123456")
                .setHeader("day", LocalDate.now())
                .build();
        stateMachine.sendEvent(orderEventsMessage);

        stateMachine.sendEvent(OrderEvents.DELIVER);
        System.out.println("Máquina de estados finalizada");
    }
}

package com.example.statemachine.actions;

import com.example.statemachine.events.OrderEvents;
import com.example.statemachine.states.OrderStates;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

@Configuration
@Log4j2
public class ShippedAction {
    static Logger logger = LoggerFactory.getLogger(ShippedAction.class);

    @Bean
    public static Action<OrderStates, OrderEvents> sendEmail() {
        return context -> logger.info("Pedido numero: " + context.getMessageHeader("orderNumber") + ", Email para informar envio do pedido");
    }
}

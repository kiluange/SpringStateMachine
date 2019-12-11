package com.example.statemachine.config;

import com.example.statemachine.actions.ShippedAction;
import com.example.statemachine.events.OrderEvents;
import com.example.statemachine.states.OrderStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class MachineConfig extends EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(Boolean.TRUE)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states
                .withStates()
                .initial(OrderStates.CREATED)
                .state(OrderStates.SHIPPED, ShippedAction.sendEmail(), null)
                .states(EnumSet.allOf(OrderStates.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStates.CREATED).target(OrderStates.APPROVED)
                .event(OrderEvents.CONFIRMED_PAYMENT)
                .and().withExternal()
                .source(OrderStates.APPROVED).target(OrderStates.INVOICED)
                .event(OrderEvents.INVOICE_ISSUED)
                .and().withExternal()
                .source(OrderStates.APPROVED).target(OrderStates.CANCELLED)
                .event(OrderEvents.CANCEL)
                .and().withExternal()
                .source(OrderStates.INVOICED).target(OrderStates.SHIPPED)
                .event(OrderEvents.SHIP).guard(onlyWorkingDays())
                .and().withExternal()
                .source(OrderStates.SHIPPED).target(OrderStates.DELIVERED)
                .event(OrderEvents.DELIVER)
        ;
    }


    @Bean
    public StateMachineListener<OrderStates, OrderEvents> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
                System.out.println("OrderState change from " + from.getId() + " to " + to.getId());
            }
        };
    }

    @Bean
    public Guard<OrderStates, OrderEvents> onlyWorkingDays() {
        return context -> !EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(
                ((LocalDate) context.getMessageHeader("day")).getDayOfWeek());
    }
}

package com.example.demo;


import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableStateMachineFactory
public class SimpleStateMachineConfiguration3
        extends StateMachineConfigurerAdapter<String, String> {

    public static AtomicInteger sum = new AtomicInteger();

    public static void print(String msg) {
        System.out.println("[" + System.currentTimeMillis() + "][" + Thread.currentThread().getName() + "] " + msg);
    }

    public Action<String, String> print(int i) {
        return stateContext -> {
            sum.incrementAndGet();
            print("----------- cur state = " +stateContext.getStateMachine().getState().getId() + " "+ i);
        };
    }

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states)
            throws Exception {

        states.withStates()
                .initial("START")
                .end("END")
                .states(new HashSet<>(Arrays.asList("S1", "S2", "S3")))
                .state("S1", "E3"); // E3 is deferred in state S1


    }


    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {


        transitions
                .withExternal()
                .source("START").target("S1").event("E1").and()
                .withExternal()
                .source("S1").target("S2").timerOnce(10)
                .action(print(2))
                .and()
                .withExternal()
                .source("S2").target("S3").event("E3")
                .action(print(3))
                .and()
                .withExternal()
                .source("S2").target("S2").event("TRIGGERDEFERRED")
                .action(print(4)) // this is just to trigger the deferred event

        ;

    }


}


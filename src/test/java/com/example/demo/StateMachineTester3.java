package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import java.util.concurrent.BrokenBarrierException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SimpleStateMachineConfiguration3.class)
public class StateMachineTester3 {

    @Autowired
    private StateMachineFactory<String, String> stateMachineFactory;

    @Test
    public void testState1() throws InterruptedException, BrokenBarrierException {


		StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine("test" );

		stateMachine.addStateListener(new StateMachineListenerAdapter<>() {
			private StateContext<String, String> stateContext;

			@Override
			public void stateContext(StateContext<String, String> stateContext) {
				this.stateContext = stateContext;
			}

			@Override
			public void eventNotAccepted(Message<String> event) {
				SimpleStateMachineConfiguration3.print("----------- EventNotAccepted: Payload: " + event.getPayload() + " state:" + stateContext.getStateMachine().getState().getId());
			}

		});
		stateMachine.start();


		stateMachine.sendEvent("E1");  // Go from Start to S1 state

		Thread.sleep(2);

		stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload("E3").build())).subscribe(ret -> {
			System.out.println(ret.getResultType()); // this event is deferred
		});

		// uncomment this to see how the deferred event E3 is activated
		/*Thread.sleep(100);
		stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload("TRIGGERDEFERRED").build())).subscribe(ret -> {
			System.out.println(ret.getResultType()); // this event will 
		});*/

		Thread.sleep(3000);
		SimpleStateMachineConfiguration3.print("--- cur state = " + stateMachine.getState().getId() + " ---");
    }
}

package com.taotao.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
/**
 * 接收ActiveMQ发送的消息
 * @author
 *
 */
public class MyMessageListener implements MessageListener{

	@Override
	public void onMessage(Message message) {
		
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println(textMessage.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

package com.taotao.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestActiveMq {

	/**
	 * 测试点对点,生产者
	 * 
	 * @throws Exception
	 */
	@Test
	public void testP2P() throws Exception {
		// 1.创建一个连接工厂对象ConnectionFactory对象。需要指定mq服务的ip及端口号。
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.136.128:61616");
		// 2.使用ConnectionFactory创建一个连接Connection对象
		Connection connection = connectionFactory.createConnection();
		// 3.开启连接。调用Connection对象的start方法
		connection.start();
		// 4.使用Connection对象创建一个Session对象
		// 第一个参数是是否开启事务，一般不使用事务
		// 如果第一个参数为true，那么第二个参数将会被忽略掉。如果不开启事物false，第二个参数为消息的应答模式
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// 5.使用Session对象创建一个Destination对象，两种形式queue、topic。现在我们使用queue
		// 参数就是消息队列的名称
		Queue queue = session.createQueue("test-queue");
		// 6.使用Session对象创建一个Producer对象
		MessageProducer producer = session.createProducer(queue);

		// 7.创建一个TextMessage对象
		TextMessage textMessage = session.createTextMessage("hello,123");
		// 8.发送消息
		producer.send(textMessage);
		// 9.关闭资源
		producer.close();
		session.close();
		connection.close();

	}

	/**
	 * 测试点对点,消费者
	 * 
	 * @throws Exception
	 */
	@Test
	public void testQueueConsumer() throws Exception {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.136.128:61616");

		Connection connection = connectionFactory.createConnection();
		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue queue = session.createQueue("test-queue");
		// 使用Session对象创建一个Consumer对象
		MessageConsumer consumer = session.createConsumer(queue);
		// 向Consumer对象中设置一个MessageListener对象，用来接收消息
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {

				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					try {
						System.out.println(textMessage.getText());
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		System.in.read();

		consumer.close();
		session.close();
		connection.close();

	}

	/**
	 * 测试发布-订阅 生产者
	 * 
	 * @throws JMSException
	 */
	@Test
	public void testTopicProducer() throws Exception {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.136.128:61616");

		Connection connection = connectionFactory.createConnection();

		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 使用Session对象创建一个Destination对象，两种形式queue、topic。现在我们使用topic
		Topic topic = session.createTopic("test-topic");

		// 使用Session对象创建一个Producer对象

		MessageProducer producer = session.createProducer(topic);
		// 创建一个TextMessage对象
		TextMessage textMessage = session.createTextMessage("hello activemq topic");
		// 发送消息
		producer.send(textMessage);

		producer.close();
		session.close();
		connection.close();

	}

	/**
	 * 测试发布-订阅 消费者
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTopicConsumer() throws Exception {

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.136.128:61616");

		Connection connection = connectionFactory.createConnection();

		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Topic topic = session.createTopic("test-topic");

		MessageConsumer consumer = session.createConsumer(topic);

		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {

				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					try {
						System.out.println(textMessage.getText());
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
		System.out.println("消费者3...");
		System.in.read();
		
		consumer.close();
		session.close();
		connection.close();


	}

}

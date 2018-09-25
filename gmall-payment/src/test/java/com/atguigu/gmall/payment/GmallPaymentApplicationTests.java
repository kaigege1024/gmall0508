package com.atguigu.gmall.payment;

import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPaymentApplicationTests {

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Test
    public void contextLoads() {

        ConnectionFactory Connect = new ActiveMQConnectionFactory("tcp://192.168.64.123:61616");

        try {
            Connection connection = Connect.createConnection();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            Queue queue = session.createQueue("TEST1");

            MessageProducer producer = session.createProducer(queue);

            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();

            textMessage.setText("今天天气不错");

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            producer.send(textMessage);

            session.commit();

            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
        @Test
        public void testConsumer () {

            ConnectionFactory connect = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.64.123:61616");

            Connection connection = null;
            try {
                connection = connect.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue queue = session.createQueue("TEST1");
                MessageConsumer consumer = session.createConsumer(queue);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        if (message instanceof TextMessage){
                            String text = null;
                            try {
                                text = ((TextMessage) message).getText();
                                System.err.println(text);
                                System.err.println("hello world");
                            } catch (JMSException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            } catch (JMSException e) {
                e.printStackTrace();
            }





        }

    @Test
    public void test () {

        Connection connection = activeMQUtil.getConnection();

        System.err.println(connection);


    }

    }
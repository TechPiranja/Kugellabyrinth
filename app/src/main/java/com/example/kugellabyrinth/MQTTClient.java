package com.example.kugellabyrinth;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTClient {

    private static final String sub_topic = "sensor/data";      // ggf. Anpassen
    private static final String pub_topic = "sensehat/message"; // ggf. Anpassen
    private int qos = 0; // MQTT quality of service
    private String data;
    private String clientId;
    private MemoryPersistence persistence;
    private MqttClient client;

    private static MQTTClient instance;

    public MQTTClient() {
        persistence = new MemoryPersistence();
    }

    public static MQTTClient getInstance () {
        if (MQTTClient.instance == null) {
            MQTTClient.instance = new MQTTClient();
        }
        return MQTTClient.instance;
    }
    /**
     * Connect to broker and
     * @param broker Broker to connect to
     */
    public void connect (String broker) {
        try {
            clientId = MqttClient.generateClientId();
            client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Log.d("TEST", "Connecting to broker: " + broker);
            client.connect(connOpts);
            Log.d("TEST", "Connected with broker: " + broker);
        } catch (MqttException me) {
            Log.e("TEST", "Reason: " + me.getReasonCode());
            Log.e("TEST", "Message: " + me.getMessage());
            Log.e("TEST", "localizedMsg: " + me.getLocalizedMessage());
            Log.e("TEST", "cause: " + me.getCause());
            Log.e("TEST", "exception: " + me);
        }
    }

    /**
     * Subscribes to a given topic
     * @param topic Topic to subscribe to
     */
    public void subscribe(String topic) {
        try {
            client.subscribe(topic, qos, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage msg) throws Exception {
                    String message = new String(msg.getPayload());
                    Log.d("TEST", "Message with topic " + topic + " arrived: " + message);
                }
            });
            Log.d("TEST", "subscribed to topic " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Publishes a message via MQTT (with fixed topic)
     * @param topic topic to publish with
     * @param msg message to publish with publish topic
     */
    public void publish(String topic, String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unsubscribe from default topic (please unsubscribe from further
     * topics prior to calling this function)
     */
    public void disconnect() {
        try {
            client.unsubscribe(sub_topic);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("TEST", e.getMessage());
        }
        try {
            Log.d("TEST", "Disconnecting from broker");
            client.disconnect();
            Log.d("TEST", "Disconnected.");
        } catch (MqttException me) {
            Log.e("TEST", me.getMessage());
        }
    }
}
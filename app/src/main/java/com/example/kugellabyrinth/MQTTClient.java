package com.example.kugellabyrinth;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * The type Mqtt client.
 */
public class MQTTClient {

    private static final String sub_topic = "sensor/data";
    private static final String pub_topic = "sensehat/message";
    /**
     * The constant qos.
     */
    public static int qos = 0; // MQTT quality of service
    private String clientId;
    private MemoryPersistence persistence;
    /**
     * The Client.
     */
    public MqttClient client;
    /**
     * The constant usingMQTT.
     */
    public static Boolean usingMQTT = false;
    /**
     * The constant serverUri.
     */
    public static String serverUri = "";

    private static MQTTClient instance;

    /**
     * Instantiates a new Mqtt client.
     */
    public MQTTClient() {
        persistence = new MemoryPersistence();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MQTTClient getInstance () {
        if (MQTTClient.instance == null) {
            MQTTClient.instance = new MQTTClient();
        }
        return MQTTClient.instance;
    }

    /**
     * Connect.
     */
    public void connect () {
        try {
            clientId = MqttClient.generateClientId();
            client = new MqttClient(serverUri, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            Log.d("TEST", "Connecting to broker: " + serverUri);
            client.connect(connOpts);
            Log.d("TEST", "Connected with broker: " + serverUri);
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
     *
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
     *
     * @param topic topic to publish with
     * @param msg   message to publish with publish topic
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
     * Unsubscribe all.
     */
    public void unsubscribeAll(){
        try {
            client.unsubscribe(sub_topic);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("TEST", e.getMessage());
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
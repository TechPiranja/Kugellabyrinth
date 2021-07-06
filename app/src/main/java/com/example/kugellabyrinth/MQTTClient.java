package com.example.kugellabyrinth;

import android.util.Log;
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
    public static MqttClient client;
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
            connOpts.setConnectionTimeout(1);
            client.connect(connOpts);
            Log.d("MQTT", "Connected with broker: " + serverUri);
        } catch (MqttException me) {
            Log.e("MQTT", "Reason: " + me.getReasonCode());
            Log.e("MQTT", "Message: " + me.getMessage());
            Log.e("MQTT", "localizedMsg: " + me.getLocalizedMessage());
            Log.e("MQTT", "cause: " + me.getCause());
            Log.e("MQTT", "exception: " + me);
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
     * Unsubscribe from default topic (please unsubscribe from further
     * topics prior to calling this function)
     */
    public void disconnect() {
        try {
            client.unsubscribe(sub_topic);
            Log.d("MQTT", "unsubscribed topic");
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("MQTT", e.getMessage());
        }
        try {
            Log.d("MQTT", "Disconnecting from broker");
            client.disconnect();
            Log.d("MQTT", "Disconnected.");
        } catch (MqttException me) {
            Log.e("MQTT", me.getMessage());
        }
    }
}
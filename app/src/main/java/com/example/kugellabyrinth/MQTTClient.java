package com.example.kugellabyrinth;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;

public class MQTTClient {

    MqttClient client;
    private MemoryPersistence persistence;

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

    public void connect (String broker, Context context) {
        String serverUri = "tcp://" + broker + ":8883";
        String clientId = MqttClient.generateClientId();

        final MqttAndroidClient client =
                new MqttAndroidClient(context, serverUri,
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("TEST", "onSuccess");

                    String topic = "foo/bar";
                    String payload = "the payload";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        //client.publish(topic, message);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TEST", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
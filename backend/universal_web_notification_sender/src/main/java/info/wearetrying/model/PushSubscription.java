package info.wearetrying.model;
/*
 * Created by Lahiru Jayakody on 11/28/2016.
 */

import org.json.*;

import java.io.Serializable;

public class PushSubscription implements Serializable {
    
    private String endpoint;
    private String p256dh;
    private String auth;

    public PushSubscription(String endpoint, String p256dh, String auth) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
    }

    public PushSubscription(String json) {
        JSONObject obj = new JSONObject(json);
        endpoint = obj.getString("endpoint");
        JSONObject keys = obj.getJSONObject("keys");
        p256dh = keys.getString("p256dh");
        auth = keys.getString("auth");
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getP256dh() {
        return p256dh;
    }

    public void setP256dh(String p256dh) {
        this.p256dh = p256dh;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}

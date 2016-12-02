package info.wearetrying.model;
/*
 * Created by Lahiru Jayakody on 11/28/2016.
 */

import org.json.JSONObject;

public class PushPayload extends JSONObject {
    
    private String title;
    private String message;

    public PushPayload(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public String toString(){
        return new JSONObject()
                .append("title", title)
                .append("message", message)
                .toString();
    }
}

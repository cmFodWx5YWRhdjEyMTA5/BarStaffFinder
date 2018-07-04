package com.cavaliers.mylocalbartender.server;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Robert on 13/03/2017.
 */

public class MLBImageRequest extends ImageRequest {

    public MLBImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
    }

    /**
     *
     * @param volleyError The error object to be parsed before passed to the deliverError method
     * @return The parsed error message to be delivered with the deliverError(VolleyError) method
     */
    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if(volleyError != null && volleyError.networkResponse != null) {
            try {
                String jsonString = new String(volleyError.networkResponse.data);
                JSONObject jsonError = new JSONObject(jsonString);
                return new VolleyError(jsonError.getString("error_message"));
            } catch (JSONException e) {
                return super.parseNetworkError(volleyError);
            }
        }
        return new VolleyError("Error something went wrong, please try again later");
    }
}

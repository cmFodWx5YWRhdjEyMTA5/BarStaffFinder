package com.cavaliers.mylocalbartender.server;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Robert on 16/02/2017.
 */

public class MLBJsonObjectRequest extends JsonObjectRequest {

    /**
     * The time when the cache will expire
     */
    private static final int CACHE_EXPIRATION;

    static {
        CACHE_EXPIRATION = 3 * 60 * 1000;
        //cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        // cacheExpired = 3 * 60 * 1000; // in 24 hours this cache entry expires completely
    }

    /**
     *
     * @param method The HTTP method to be used
     * @param url The url to send the request to
     * @param jsonRequest The JSON request to be embedded in the body
     * @param listener The listener on success requests
     * @param errorListener The listener on unsuccessful request
     * @param toCache Whether to cache the request or not
     */
    public MLBJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, boolean toCache) {
        super(method, url, jsonRequest, listener, errorListener);
        this.setShouldCache(toCache);
    }

    /**
     *
     * @param url The url to send the request to
     * @param jsonRequest The JSON request to be embedded in the body
     * @param listener The listener on success requests
     * @param errorListener The listener on unsuccessful request
     * @param toCache Whether to cache the request or not
     */
    public MLBJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, boolean toCache) {
        super(url, jsonRequest, listener, errorListener);
        this.setShouldCache(toCache);
    }

    /**
     *
     * @param response The response return by the MBL RESTFul server
     * @return The parsed response to be forward to the deliverResponse method
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        if(!this.shouldCache()){
            return super.parseNetworkResponse(response);
        }else {
            try {
                Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                if (cacheEntry == null) {
                    cacheEntry = new Cache.Entry();
                }
                final long cacheHitButRefreshed = CACHE_EXPIRATION;
                final long cacheExpired = CACHE_EXPIRATION;
                long now = System.currentTimeMillis();
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;
                cacheEntry.data = response.data;

                cacheEntry.softTtl = softExpire;
                cacheEntry.ttl = ttl;
                String headerValue;
                headerValue = response.headers.get("Date");
                if (headerValue != null) {
                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                headerValue = response.headers.get("Last-Modified");
                if (headerValue != null) {
                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                cacheEntry.responseHeaders = response.headers;
                final String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new JSONObject(jsonString), cacheEntry);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException e) {
                return Response.error(new ParseError(e));
            }
        }
    }

    /**
     *
     * @param response Contains the json response when successful request is made to the MLB server
     */
    @Override
    protected void deliverResponse(JSONObject response) {
        super.deliverResponse(response);
    }

    /**
     *
     * @param error Error object that is delivered when error occurs
     */
    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
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

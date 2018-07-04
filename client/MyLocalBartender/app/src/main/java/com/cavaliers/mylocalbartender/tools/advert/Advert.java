package com.cavaliers.mylocalbartender.tools.advert;

import com.cavaliers.mylocalbartender.server.MLBService;

import java.util.HashMap;

public class Advert {

    private HashMap<String,String> properties;

    private Advert(){

        this.properties = new HashMap<>();
    }

    public void put(String key, String value){

        properties.put(key,value);
    }

    public String getValue(String key){

        return this.properties.get(key);
    }

    public static AdvertBuilder builder(){

        return new AdvertBuilder();
    }

    public static class AdvertBuilder{

        private Advert advert;

        private AdvertBuilder(){

            this.advert = new Advert();
        };

        public AdvertBuilder put(String key, String value){

            advert.properties.put(key,value);
            return this;
        }

        public Advert build(){

            return advert;
        }
    }

    public class Test{

        public Test(){

            Advert.builder()
                    .put(MLBService.JSONKey.CHAT_MESSAGE_KEY,"")
                    .put(MLBService.JSONKey.ACCOUNT_TYPE,"")
                    .build();
        }

    }
}

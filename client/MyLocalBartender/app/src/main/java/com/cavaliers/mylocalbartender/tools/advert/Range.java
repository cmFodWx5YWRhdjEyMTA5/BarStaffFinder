package com.cavaliers.mylocalbartender.tools.advert;

public class Range{

    private int starting;
    private int ending;

    public Range(){}

    public Range(int starting,int ending){
        this.starting = starting;
        this.ending = ending;
    }

    public void setRange(int starting,int ending){
        this.starting = starting;
        this.ending = ending;
    }
    public int getStart(){
        return this.starting;
    }
    public int getEnd(){
        return this.ending;
    }

}
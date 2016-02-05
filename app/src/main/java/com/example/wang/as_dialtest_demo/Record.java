package com.example.wang.as_dialtest_demo;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by wang on 2016/2/5.
 */
public class Record {

    public int id;
    public long time;
    public String network;
    public String authn;
    public int result;

    public Record() {
    }

    public Record(long time, String network, String authn, int result) {
        this.time = time;
        this.network = network;
        this.authn = authn;
        this.result = result;
    }

    public Record(int id, long time, String network, String authn, int result) {
        this.id = id;
        this.time = time;
        this.network = network;
        this.authn = authn;
        this.result = result;
    }



    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAuthn() {
        return authn;
    }

    public void setAuthn(String authn) {
        this.authn = authn;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

        return "{" +
                "id=" + id +
                ", time=" + simpleDateFormat.format(new Date(time)) +
                ", network='" + network + '\'' +
                ", authn='" + authn + '\'' +
                ", result=" + result +
                '}';



    }


}

package com.example.fitnessapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Record {
    private String documentId;
    private String weight;
    private String height;
    private Date date;
    private long timestamp;

    public Record(){

    }

    @Exclude
    public String getDocumentId(){
        return documentId;
    }

    public void setDocumentId(String documentId){
        this.documentId = documentId;
    }

    public Record(String weight, String height, long timestamp, Date date) {
        this.weight = weight;
        this.height = height;
        this.date = date;
        this.timestamp = timestamp;
    }


    public String getWeight(){
        return weight;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public Date getDate(){
        return date;
    }

    public String getHeight(){
        return height;
    }
}

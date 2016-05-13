package com.saceone.tfg.Classes;

/**
 * Created by ASUS on 10/03/2016.
 */
public class TagRoom {

    private int id;
    private int room;
    private String tag;

    public TagRoom(int id, int room, String tag){
        this.id=id;
        this.room=room;
        this.tag=tag;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public int getRoom(){
        return room;
    }
    public void setRoom(int room){
        this.room=room;
    }

    public String getTag(){
        return tag;
    }
    public void setTag(String tag){
        this.tag = tag;
    }
}

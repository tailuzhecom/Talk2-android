package com.example.young.talk2;

/**
 * Created by young on 18-5-1.
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;
    private String name;
    private String content;
    private int type;
    private int iconId;

    public Msg(String content, int type, int iconId, String name) {
        this.content = content;
        this.type = type;
        this.iconId = iconId;
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public int getIconId() { return iconId; }

    public String getName() { return name; }
}

package com.example.young.talk2;

/**
 * Created by young on 18-5-24.
 */

public class NoteItem {
    private String mName;
    private String mNote;
    private int mImageId;
    private String mexpressionId;

    public NoteItem(String name, int imageId, String note, String expressionId) {
        this.mName = name;
        this.mImageId = imageId;
        this.mNote = note;
        this.mexpressionId = expressionId;
    }

    public String getName() {
        return mName;
    }

    public String getNote() {

        return mNote;
    }

    public int getImageId() {
        return mImageId;
    }

    public String getExpressionId() {
        return mexpressionId;
    }
}

package com.example.young.talk2;

import android.widget.TextView;

/**
 * Created by young on 18-5-1.
 */

public class FriendItem {
    private String mName;
    private String mNote;
    private int mImageId;

    public FriendItem(String name, int imageId, String note) {
        this.mName = name;
        this.mImageId = imageId;
        this.mNote = note;
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
}

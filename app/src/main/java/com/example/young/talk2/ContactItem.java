package com.example.young.talk2;

/**
 * Created by young on 18-5-22.
 */

public class ContactItem {
    private String mName;
    private int mImageId;

    public ContactItem(String name, int imageId) {
        this.mName = name;
        this.mImageId = imageId;
    }

    public String getName() {
        return mName;
    }

    public int getImageId() {
        return mImageId;
    }
}

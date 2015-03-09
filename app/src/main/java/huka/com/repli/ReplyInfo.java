package huka.com.repli;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


/**
 * Created by hugo on 3/6/15.
 */
public class ReplyInfo {
    private String username;
    private String date;
    private boolean replied;
    private Bitmap profilePicture;
    private Bitmap thumbnail;
    private Bitmap image;

    public ReplyInfo(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isReplied() {
        return replied;
    }

    public void setReplied(boolean replied) {
        this.replied = replied;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}

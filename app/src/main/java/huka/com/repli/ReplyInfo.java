package huka.com.repli;

import android.graphics.drawable.Drawable;


/**
 * Created by hugo on 3/6/15.
 */
public class ReplyInfo {
    private String username;
    private String date;
    private boolean replied;
    private Drawable profilePicture;
    private Drawable thumbnail;
    private Drawable image;

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

    public Drawable getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Drawable profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}

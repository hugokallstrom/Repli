package huka.com.repli;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


/**
 * Temporary class used to hold info about a user.
 */
public class ReplyInfo {
    private String username;
    private String date;
    private boolean replied;
    private String profilePicture;
    private String thumbnail;
    private String image;
    private Bitmap bitmapImage;

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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBitmapImage(Bitmap image) {
        bitmapImage = image;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }
}

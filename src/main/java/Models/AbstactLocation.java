package Models;

import com.google.cloud.firestore.annotation.Exclude;
import javafx.scene.image.ImageView;

abstract public class AbstactLocation {
    protected ImageView view;
    protected String color = "";
    protected Boolean available;

    @Exclude
    public ImageView getView() {
        return view;
    }

    @Exclude
    public void setView(ImageView view) {
        this.view = view;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}

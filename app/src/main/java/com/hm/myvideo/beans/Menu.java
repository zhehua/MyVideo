package com.hm.myvideo.beans;

import java.util.List;

public class Menu {
    String category;
    List<PlayItem> items;
    String vodPic;
    String vodRemarks;

    public String getVodRemarks() {
        return vodRemarks;
    }

    public void setVodRemarks(String vodRemarks) {
        this.vodRemarks = vodRemarks;
    }

    public String getVodPic() {
        return vodPic;
    }

    public void setVodPic(String vodPic) {
        this.vodPic = vodPic;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<PlayItem> getItems() {
        return items;
    }

    public void setItems(List<PlayItem> items) {
        this.items = items;
    }
}

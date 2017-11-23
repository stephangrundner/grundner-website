package biz.grundner.story.chapter;

import biz.grundner.story.Photo;

/**
 * @author Stephan Grundner
 */
public class PhotoWithTextChapter extends Chapter {

    public enum Layout {
        PHOTO_LEFT,
        TEXT_LEFT,
        STACKED
    }

    private Photo photo;
    private String title;
    private String subtitle;
    private String text;
    private Layout layout;

    @Override
    public String getTemplate() {
        return "story/chapter/photoWithText";
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}

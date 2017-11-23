package biz.grundner.story.chapter;

import biz.grundner.story.Photo;

/**
 * @author Stephan Grundner
 */
public class PhotoOnlyChapter extends Chapter {

    public enum Type {
        STANDARD,
        WIDE,
        TALL,
        FULL
    }

    private Photo photo;
    private Type type;

    @Override
    public String getTemplate() {
        return "story/chapter/photoOnly";
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

package biz.grundner.story.chapter;

import biz.grundner.story.Photo;

import java.util.List;

/**
 * @author Stephan Grundner
 */
public class ThreePhotosChapter extends Chapter {

    public enum Layout {
        TWO_TO_ONE,
        ONE_TO_TWO
    }

    private List<Photo> photos;
    private Layout layout;

    @Override
    public String getTemplate() {
        return "story/chapter/threePhotos";
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}

package biz.grundner.story.chapter;

import biz.grundner.story.Story;

/**
 * @author Stephan Grundner
 */
public abstract class Chapter {

    private Story story;

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public abstract String getTemplate();
}

package biz.grundner.story.chapter;

/**
 * @author Stephan Grundner
 */
public class TextOnlyChapter extends Chapter {

    private String title;
    private String subtitle;
    private String text;

    @Override
    public String getTemplate() {
        return "story/chapter/textOnly";
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
}

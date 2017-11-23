package biz.grundner.story;

import biz.grundner.story.chapter.*;
import biz.jovido.seed.content.*;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stephan Grundner
 */
public class StoryModelFactory implements ModelFactory {

    private final ItemService itemService;

    @Override
    public boolean supports(Structure structure) {
        return "story".equals(structure.getName());
    }

    PhotoOnlyChapter createPhotoOnlyChapter() {

        return null;
    }

    @Override
    public void apply(Item item, Model model) {
        Story story = new Story();

        TextPayload titlePayload = (TextPayload)
                itemService.getPayload(item, "title");
        story.setTitle(titlePayload.getText());

        TextPayload subtitlePayload = (TextPayload)
                itemService.getPayload(item, "subtitle");
        story.setSubtitle(subtitlePayload.getText());

        TextPayload summaryPayload = (TextPayload)
                itemService.getPayload(item, "summary");
        story.setSummary(summaryPayload.getText());

        List<Chapter> chapters = new ArrayList<>();
        List<Item> chapterItems = itemService.getNestedItems(item, "chapters");
        Chapter chapter;
        for (Item chapterItem : chapterItems) {
            switch (chapterItem.getStructureName()) {
                case "textOnlyChapter":
                    chapter = new TextOnlyChapter();
                    break;
                case "photoOnlyChapter":
                    chapter = createPhotoOnlyChapter();
                    break;
                case "threePhotosChapter":
                    chapter = new ThreePhotosChapter();
                    break;
                case "photoWithTextChapter":
                    chapter = new PhotoWithTextChapter();
                    break;
                default:
                    throw new RuntimeException("Unexpected structure: " + chapterItem.getStructureName());
            }

            chapter.setStory(story);

            chapters.add(chapter);
        }
        story.setChapters(chapters);

        model.addAttribute("story", story);
    }

    public StoryModelFactory(ItemService itemService) {
        this.itemService = itemService;
    }
}

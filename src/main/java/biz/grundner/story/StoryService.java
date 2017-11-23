package biz.grundner.story;

import biz.jovido.seed.content.Item;
import biz.jovido.seed.content.ItemService;
import biz.jovido.seed.content.ItemVisitResult;
import biz.jovido.seed.content.SimpleItemVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Stephan Grundner
 */
@Service
public class StoryService {

    @Autowired
    private ItemService itemService;

    public List<Item> getPhotos(Item item) {
        List<Item> photos = new ArrayList<>();

        itemService.walkItem(item, new SimpleItemVisitor() {
            @Override
            public ItemVisitResult visitItem(Item item) {
                if ("photo".equals(item.getStructureName())) {
                    photos.add(item);
                }
                return super.visitItem(item);
            }
        });

        return Collections.unmodifiableList(photos);
    }
}

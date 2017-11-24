package biz.grundner.story;

import biz.jovido.seed.content.Item;
import biz.jovido.seed.content.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * @author Stephan Grundner
 */
@Controller
@RequestMapping(path = {"/", "/stories/"})
public class StoryController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private StoryService storyService;

    @Autowired
    private EntityManager entityManager;

    @RequestMapping
    protected String index(Model model) {

        String qlString = "select l.published " +
                "from Leaf l " +
                "join l.published i " +
                "where i != null " +
                "and i.structureName = ?1 " +
                "order by i.createdAt desc";
        TypedQuery<Item> query = entityManager.createQuery(qlString, Item.class);
        query.setParameter(1, "story");

        Model stories = new ExtendedModelMap();
        for (Item item : query.getResultList()) {
            itemService.itemToModel(item, stories);
        }

        model.addAttribute("stories", stories);
        model.addAttribute("label", "Stephan Grundner");

        return "stories/index";
    }
}

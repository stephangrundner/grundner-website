package biz.grundner;

import biz.jovido.seed.content.Item;
import biz.jovido.seed.content.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stephan Grundner
 */
@Controller
@RequestMapping(path = {"/", "/stories/"})
public class StoriesController {

    @Autowired
    private ItemService itemService;

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

        List stories = new ArrayList();
        for (Item item : query.getResultList()) {
            stories.add(itemService.toMap(item));
        }

        model.addAttribute("stories", stories);
        model.addAttribute("label", "Stephan Grundner");

        return "stories/index";
    }
}

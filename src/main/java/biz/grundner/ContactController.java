package biz.grundner;

import biz.jovido.seed.content.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;

/**
 * @author Stephan Grundner
 */
@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    @RequestMapping
    protected String index(Model model) {

        model.addAttribute("label", "Kontakt");
        return "contact/index";
    }
}

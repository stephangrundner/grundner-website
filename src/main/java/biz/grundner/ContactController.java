package biz.grundner;

import biz.jovido.seed.content.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Stephan Grundner
 */
@Controller
@RequestMapping("/contact/")
public class ContactController {

    private static final Logger LOG = LoggerFactory.getLogger(ContactController.class);

    public static class Message {

        @Pattern(message = "{grundner.contact.message.name.invalid}",
                regexp = "(\\s*[a-zA-Z0-9_.-]+\\s*){3,}")
        private String name;

        @Pattern(message = "{grundner.contact.message.email.invalid}",
                regexp = "(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$)")
        private String email;

        @Pattern(message = "{grundner.contact.message.phone.invalid}",
                regexp = "(^\\+?((\\s*[0-9])+\\s*-?){8,})?")
        private String phone;

        @Size(message = "{grundner.contact.message.text.invalid}", min = 8, max = 255 * 16)
        private String text;

        @Size(message = "{grundner.contact.message.important.invalid}", max = 0)
        private String important;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImportant() {
            return important;
        }

        public void setImportant(String important) {
            this.important = important;
        }
    }

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Validator validator;

    @Autowired
    public JavaMailSender mailSender;

    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(validator);
    }

    @ModelAttribute
    protected Message message() {
        Message message = new Message();

        return message;
    }

    @RequestMapping
    protected String index(Model model) {

        model.addAttribute("label", "Kontakt");

        return "contact/index";
    }

    @PostMapping(path = "send")
    protected String send(@Valid @ModelAttribute Message message,
                          BindingResult messageBinding,
                          RedirectAttributes redirectAttributes) {

        if (messageBinding.hasErrors()) {
            return "contact/index";
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("stephan.grundner@gmail.com");
            mail.setFrom("stephan.grundner@gmail.com");
            mail.setReplyTo(String.format("\"%s\" <%s>", message.name, message.email));
            mail.setSubject(String.format("Nachricht von %s ", message.name));

            StringBuilder textBuilder = new StringBuilder()
                    .append(message.text);

            if (StringUtils.hasText(message.phone)) {
                textBuilder.append("\n\r\n")
                        .append(message.phone);
            }

            mail.setText(textBuilder.toString());

            mailSender.send(mail);
        } catch (Exception e) {
            LOG.error("Error on sending mail", e);
        }

        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:";
    }
}

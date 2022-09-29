package edu.pe.lamolina.admision.controller.website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.edu.lamolina.model.cms.Post;
import pe.edu.lamolina.model.cms.enums.PostEnum;

@Controller
public class WebsiteController {

    @Autowired
    WebsiteService service;

    @RequestMapping("/")
    public String page(Model model) {

        Post post = service.findPostByURL("inicio");

        model.addAttribute("post", post);
        return String.format("website/%s", post.getTemplate());
    }

    @RequestMapping(method = RequestMethod.GET, value = "{pageURL}")
    public String page(@PathVariable String pageURL, Model model) {

        Post post = service.findPostByURL(pageURL);
        if (post == null) {
            return "redirect:/";
        }

        if (post.getEstadoEnum() == PostEnum.DRAFT) {
            return "redirect:/";
        }

        model.addAttribute("post", post);
        return String.format("website/%s", post.getTemplate());
    }

    @RequestMapping(method = RequestMethod.GET, value = "{typeTaxonomy}/{taxonomy}")
    public String page(@PathVariable String typeTaxonomy, @PathVariable String taxonomy, Model model) {
        return "redirect:/";
    }
}

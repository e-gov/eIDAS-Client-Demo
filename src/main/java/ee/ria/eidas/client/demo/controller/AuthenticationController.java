package ee.ria.eidas.client.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AuthenticationController {

    @Value("${eidas.client.availableCountries:EE,CA,CB,CD}")
    private String[] availableCountries;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @RequestMapping(value = {"/", "/index"}, method = GET)
    public String index() {
        return "index";
    }

    @RequestMapping("/protected")
    public String protectedUrl() {
        return "protected";
    }

    @RequestMapping(value = {"/login"}, method = GET)
    public String displayLoginForm(Model model) {
        model.addAttribute("countries", Arrays.asList(availableCountries));
        model.addAttribute("loas", Arrays.asList("LOW", "SUBSTANTIAL", "HIGH"));
        return "login";
    }

}

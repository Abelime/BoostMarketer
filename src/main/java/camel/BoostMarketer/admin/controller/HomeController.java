package camel.BoostMarketer.admin.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;


@Controller
public class HomeController {
    @GetMapping(value = "/")
    public String homePage(Model model) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String id = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        model.addAttribute("id", id);
        model.addAttribute("role", role);

        return "pages/dashboard";
    }

    @ResponseBody
    @GetMapping(value = "/admin")
    public String admin() {
        return "admin page";
    }

}

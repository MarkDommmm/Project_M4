package hh.Controller;


import hh.Model.Customer;
import hh.Service.AdminService_Product;
import hh.Service.AdminService_User;
import hh.Service.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({"/", "/home"})
public class WebsiteController {

    @Autowired
    private AdminService_User adminServiceUser;

    @Autowired
    AdminService_Product adminServiceProduct;


    @GetMapping("")
    public ModelAndView home() {
        return new ModelAndView("/web/index", "ProductList", adminServiceProduct.findall());
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/web/login", "users", new Customer());
    }

    @PostMapping("/loginUser")
    public String loginU(HttpSession session, @ModelAttribute("users") Customer customer,Model model) {
        Customer c = adminServiceUser.login(customer);
        if (c == null) {
            model.addAttribute("ErrorLogin","Please login again!!!");

            return "/web/login";
        }
//        request.getSession().setAttribute("currentLogin", c);
        session.setAttribute("currentLogin", c);
        if (c.getRole() == 1){
            return "redirect:/admin/home";
        }else {
            return "redirect:/";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentLogin");
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String addUser(@ModelAttribute("users") Customer customer, Model model) {
//        if (validate.checkPasswordMatch(customer)){
        adminServiceUser.signUp(customer);

//        }else{
//            model.addAttribute("ErrorPass","Password not match");
//        }

        return "/web/login";
    }

    @GetMapping("/infoUser")
    public String infoUser() {
        return "/web/infoUser";
    }


}

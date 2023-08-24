package hh.Controller;


import hh.Model.*;
import hh.Service.AdminService_Product;
import hh.Service.AdminService_User;
import hh.Service.CartService;
import hh.Service.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.DoubleStream;

@Controller
@RequestMapping({"/", "/home"})
public class WebsiteController {

    @Autowired
    private AdminService_User adminServiceUser;

    @Autowired
    private AdminService_Product adminServiceProduct;

    @Autowired
    private CartService cartService;

    @GetMapping("")
    public ModelAndView home() {
        return new ModelAndView("/web/index", "ProductList", adminServiceProduct.findall());
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/web/login", "users", new Customer());
    }

    @PostMapping("/loginUser")
    public String loginU(HttpSession session, @ModelAttribute("users") Customer customer, Model model) {
        Customer c = adminServiceUser.login(customer);
        if (c == null) {
            model.addAttribute("Error", "Please login again!!!");
            return "/web/login";
        }
        session.setAttribute("currentLogin", c);
        if (c.getRole() == 1) {
            return "redirect:/admin/home";
        } else {
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
        boolean flag = false;
        Boolean check = adminServiceUser.checkValidate(customer);
        Boolean checkNP = adminServiceUser.check_name_pass(customer);

        if (check) {
            model.addAttribute("Error", "Username already exists");
            flag = true;
            return "/web/login";
        }
        if (checkNP) {
            model.addAttribute("Error", "Invalid username and password, please re-enter more than 6 characters");
            flag = true;
            return "/web/login";
        }
        if (!flag) {
            adminServiceUser.signUp(customer);
        }

        return "redirect:/login";

    }

    @GetMapping("/addCart/{id}")
    public String addCartItem(@PathVariable int id, HttpSession session, @RequestParam("qty") int qty, Model model) {
        Product p = adminServiceProduct.findById(id);
        CartItem cartItem = cartService.findByIdProduct(id);
        CartItem c = new CartItem();
        Customer login = (Customer) session.getAttribute("currentLogin");
        if (login == null) {
            model.addAttribute("Error", "Please login to purchase!!!");
            return "redirect:/login";
        }
        if (cartItem == null) {
            c.setId(cartService.getNewId());
            c.setProduct(p);
            c.setQuantity(qty);
            c.setPrice(p.getPrice());
            cartService.save(c);

        } else {
            cartItem.setQuantity(cartItem.getQuantity() + qty);
            cartService.save(cartItem);
        }

        List<CartItem> cartItems = cartService.findall();
        int totalQty = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        BigDecimal roundedTotalPrice = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);
        session.setAttribute("cart", cartItems);
        session.setAttribute("cartQty", totalQty);
        session.setAttribute("cartTotalPrice", roundedTotalPrice);
        return "redirect:/#buy-online";
    }

    @GetMapping("/infoUser")
    public ModelAndView infoUser() {
        return new ModelAndView("/web/infoUser", "order", new FormOrder());

    }

    @PostMapping("/order")
    public String order(@ModelAttribute("order") FormOrder formOrder, HttpSession session) {

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");

//        for (CartItem c : (List<CartItem>) session.getAttribute("cart")) {
//            System.out.println(c.getId());
//        }
        Customer customer = (Customer) session.getAttribute("currentLogin");


        BigDecimal priceBigDecimal = (BigDecimal) session.getAttribute("cartTotalPrice");
        float price = priceBigDecimal.floatValue();

        Order order = new Order();
        order.setId_customer(customer.getId());
        order.setName(formOrder.getName());
        order.setAddress(formOrder.getAddress());
        order.setPhone(formOrder.getPhone());
        order.setDescription(formOrder.getNote());
        order.setTotal_price(price);
        cartService.addOrder(cartItems, order);
        return "redirect:/";
    }


}

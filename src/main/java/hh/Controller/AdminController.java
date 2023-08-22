package hh.Controller;

import hh.Model.Customer;
import hh.Model.CustomerDto;
import hh.Model.Product;
import hh.Model.ProductDto;
import hh.Service.AdminService_Product;

import hh.Service.AdminService_User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/admin"})
@PropertySource("classpath:upload.properties")
public class AdminController {

    @Autowired
    private AdminService_User adminServiceUser;

    @Autowired
    private AdminService_Product adminService;

    @Autowired
    ServletContext context;

    @Value("${pathUpload}")
    private String pathUpload;

    //    Điều hướng về home
    @GetMapping("/home")
    public String admin() {
        return "/admin/Dashboard";
    }

    // lấy tất cả products
    @GetMapping("/products")
    public String tableproduct(Model model) {
        model.addAttribute("list", adminService.findall());
        return "/admin/TableProduct";
    }

    // Thêm products
    @GetMapping("/addProduct")
    public ModelAndView addProduct() {
        return new ModelAndView("/admin/addProducts", "product", new Product());
    }

    @PostMapping("/addProduct")
    public String save(@ModelAttribute("product") ProductDto productDto) {
        File file = new File(pathUpload);
        if (!file.exists()) {
            file.mkdir();
        }
        String urlimage = productDto.getImage().getOriginalFilename();

        try {
            FileCopyUtils.copy(productDto.getImage().getBytes(), new File(pathUpload + urlimage));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Product p = new Product();
        p.setId(productDto.getId());
        p.setNameproduct(productDto.getNameproduct());
        p.setImage(urlimage);
        p.setPrice(productDto.getPrice());
        p.setStock(productDto.getStock());
        p.setDate(productDto.getDate());
        p.setDescription(productDto.getDescription());
        p.setStatus(productDto.isStatus());
        adminService.save(p);
        return "redirect:/admin/products";
    }

    // END thêm products
//    Xóa product
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        adminService.deleteById(id);
        return "redirect:/admin/products";
    }

    //    Cập nhật product
    @GetMapping("/update/{id}")
    public ModelAndView update(@PathVariable("id") int id) {
        return new ModelAndView("/admin/updateProduct", "productUpdate", adminService.findById(id));
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("productUpdate") ProductDto productDto) {
        File file = new File(pathUpload);
        if (!file.exists()) {
            file.mkdir();
        }
        String urlimage = productDto.getImage().getOriginalFilename();

        try {
            FileCopyUtils.copy(productDto.getImage().getBytes(), new File(pathUpload + urlimage));

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Product p = new Product();
        p.setId(productDto.getId());
        p.setNameproduct(productDto.getNameproduct());
        p.setImage(urlimage);
        p.setPrice(productDto.getPrice());
        p.setStock(productDto.getStock());
        p.setDate(productDto.getDate());
        p.setDescription(productDto.getDescription());
        p.setStatus(productDto.isStatus());
        adminService.save(p);
        return "redirect:/admin/products";
    }

// lấy tất cả User
    @GetMapping("/users")
    public String tableUser(Model model) {
        model.addAttribute("list", adminServiceUser.findall());
        return "/admin/TableUser";
    }

//    // Thêm User
    @GetMapping("/addUser")
    public ModelAndView addUser() {
        return new ModelAndView("/admin/addUser", "users", new Customer());
    }

    @PostMapping("/addUser")
    public String save(@ModelAttribute("users") Customer customer) {
        adminServiceUser.save(customer);
        return "redirect:/admin/users";
    }

//    // END thêm User
////    Xóa product
    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        adminServiceUser.deleteById(id);
        return "redirect:/admin/users";
    }
//
//    //    Cập nhật User
    @GetMapping("/updateUser/{id}")
    public ModelAndView updateUser(@PathVariable("id") int id,Model model) {
        List<String> options = new ArrayList<>();
        options.add("Active");
        options.add("Inactive");
        model.addAttribute("options", options);
        return new ModelAndView("/admin/updateUser", "UserUpdate", adminServiceUser.findById(id));
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("UserUpdate") Customer customer) {
//        File file = new File(pathUpload);
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        String urlimage = customerDto.getAvatar().getOriginalFilename();
//
//        try {
//            FileCopyUtils.copy(customerDto.getAvatar().getBytes(), new File(pathUpload + urlimage));
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//        Customer c = new Customer();
//        c.setId(customerDto.getId());
//        c.setFullname(customerDto.getFullname());
//        c.setUsername(customerDto.getPassword());
//        c.setCountry(customerDto.getCountry());
//        c.setCity(customerDto.getCity());
//        c.setPhone(customerDto.getPhone());
//        c.setEmail(customerDto.getEmail());
//        c.setBirthdate(customerDto.getBirthdate());
//        c.setAvatar(urlimage);
//        c.setRole(customerDto.getRole());
//        c.setStatus(customerDto.isStatus());
        adminServiceUser.save(customer);
        return "redirect:/admin/users";
    }
}

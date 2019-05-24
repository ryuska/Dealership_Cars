package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listcategory(Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        return "list";
    }
    @GetMapping("/add")
    public String categoryform(Model model){
        model.addAttribute("car", carRepository.findAll());
        model.addAttribute("category", new Category());
        return "categoryform";

    }
    @PostMapping("/processcategory")
    public String processform(@Valid Category category, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("cars", carRepository.findAll());
            return "categoryform";
        }
        categoryRepository.save(category);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showCategory(@PathVariable("id") long id, Model model){
        model.addAttribute("category", categoryRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateCategory(@PathVariable("id") long id, Model model){
        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("category", categoryRepository.findById(id).get());
        return "categoryform";

    }
    @RequestMapping("/delete/{id}")
    public  String delCategory(@PathVariable("id") long id){
        categoryRepository.deleteById(id);
        return "redirect:/";
    }
    @RequestMapping("/addcars/{id}")
    public String addCars(@PathVariable("id") long id, Model model){
        model.addAttribute("category", categoryRepository.findById(id).get() );
        model.addAttribute("car", new Car());
        return "carform";
    }



    @PostMapping("/processcar")
    public String processForm(@RequestParam("file") MultipartFile file, @Valid Car car, BindingResult result){
        if (result.hasErrors()){
            return "carform";
        }
        try {
            Map uploadResult= cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            car.setHeadshot(uploadResult.get("url").toString());
            carRepository.save(car);
        } catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }

        return "redirect:/";

    }

}
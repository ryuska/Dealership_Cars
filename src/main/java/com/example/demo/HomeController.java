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
    DealershipRepository dealershipRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listdealership(Model model){
        model.addAttribute("dealerships", dealershipRepository.findAll());
        return "list";
    }
    @GetMapping("/add")
    public String dealershipform(Model model){
        model.addAttribute("car", carRepository.findAll());
        model.addAttribute("dealership", new Dealership());
        return "dealershipform";

    }
    @PostMapping("/processdealership")
    public String processform(@Valid Dealership dealership, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("cars", carRepository.findAll());
            return "dealershipform";
        }
        dealershipRepository.save(dealership);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showDealership(@PathVariable("id") long id, Model model){
        model.addAttribute("dealership", dealershipRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateDealership(@PathVariable("id") long id, Model model){
        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("dealership", dealershipRepository.findById(id).get());
        return "dealershipform";

    }
    @RequestMapping("/delete/{id}")
    public  String delDealership(@PathVariable("id") long id){
        dealershipRepository.deleteById(id);
        return "redirect:/";
    }
    @RequestMapping("/addcars/{id}")
    public String addCars(@PathVariable("id") long id, Model model){
        model.addAttribute("dealership", dealershipRepository.findById(id).get() );
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
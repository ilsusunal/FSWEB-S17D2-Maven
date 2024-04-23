package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.tax.DeveloperTax;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeveloperController {
    public Map<Integer, Developer> developers;
    public Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable){
        this.taxable = taxable;
    }
    @PostConstruct
    public void init(){
        this.developers = new HashMap<>();
        Developer developer = new Developer(1, "Initial Developer", 5000.0, Experience.JUNIOR);
        developers.put(developer.getId(), developer);
    }

    @GetMapping("/developers")
    public List<Developer> findAll(){
        return developers.values().stream().toList();
    }
    @GetMapping("/developers/{id}")
    public Developer findDev(@PathVariable  int id){
        Developer dev = developers.get(id);
        return dev;
    }
    @PostMapping("/developers")
    public ResponseEntity<Void> addDev(@RequestBody Developer developer){
        double taxRate;
        double salary = developer.getSalary();
        switch (developer.getExperience()){
            case JUNIOR:
                taxRate = taxable.getSimpleTaxRate();
                developer.setSalary(salary - (salary * (taxRate / 100)));
                break;
            case MID:
                taxRate = taxable.getMiddleTaxRate();
                developer.setSalary(salary - (salary * (taxRate / 100)));
                break;
            case SENIOR:
                taxRate = taxable.getUpperTaxRate();
                developer.setSalary(salary - (salary * (taxRate / 100)));
                break;
            default:
                System.out.println("Experience can not found.");
        }
        developers.put(developer.getId(), developer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(developer.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
    @PutMapping("/developers/{id}")
    public Developer updateDev(@PathVariable  int id, @RequestBody Developer developer){
        developers.put(id,
                new Developer(developer.getId(), developer.getName(), developer.getSalary(), developer.getExperience()));
        return developers.get(id);
    }
    @DeleteMapping("/developers/{id}")
    public Developer deleteDev(@PathVariable  int id){
        Developer developer = developers.get(id);
        developers.remove(developer.getId(), developer);
        return developer;
    }
}

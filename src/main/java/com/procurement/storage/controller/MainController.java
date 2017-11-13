package com.procurement.storage.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainController {


//    private CountryService countryService;
//
//    public MainController(CountryService countryService) {
//        this.countryService = countryService;
//    }
//
//    @RequestMapping(method = RequestMethod.GET)
//    public ResponseEntity<List<Country>> getCountries() {
//        List<Country> countries = countryService.getAllCountries();
//        return new ResponseEntity<>(countries, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/byCode", method = RequestMethod.GET)
//    public ResponseEntity<List<Country>> getCountriesByCode(@RequestParam String code) {
//        List<Country> countries = countryService.getCountriesByCode(code);
//        return new ResponseEntity<>(countries, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "/byName", method = RequestMethod.GET)
//    public ResponseEntity<List<Country>> getCountriesByName(@RequestParam String name) {
//        List<Country> countries = countryService.getCountriesByName(name);
//        return new ResponseEntity<>(countries, HttpStatus.OK);
//    }

}

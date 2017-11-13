package com.procurement.storage.service;

import com.procurement.storage.repository.MainRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MainServiceImpl implements MainService {

    private MainRepository countryRepository;

    public MainServiceImpl(MainRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

//    @Override
//    public List<com.procurement.mdm.model.entity.Main> getAllCountries() {
//        return countryRepository.findAll();
//    }
//
//    @Override
//    public List<com.procurement.mdm.model.entity.Main> getCountriesByCode(String code) {
//        Objects.requireNonNull(code);
//        return countryRepository.findCountriesByCode(code);
//    }
//
//    @Override
//    public List<com.procurement.mdm.model.entity.Main> getCountriesByName(String name) {
//        Objects.requireNonNull(name);
//        return countryRepository.findCountriesByName(name);
//    }
}

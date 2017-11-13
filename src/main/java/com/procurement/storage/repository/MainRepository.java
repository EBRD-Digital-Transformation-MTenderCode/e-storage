package com.procurement.storage.repository;

import com.procurement.storage.model.entity.Main;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainRepository extends JpaRepository<Main, Long> {

//    List<com.procurement.mdm.model.entity.Main> findCountriesByCode(String code);
//
//    List<com.procurement.mdm.model.entity.Main> findCountriesByName(String name);
}

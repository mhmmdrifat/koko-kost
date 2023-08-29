package id.ac.ui.cs.advprog.kost.rent.service;

import java.util.List;

import id.ac.ui.cs.advprog.kost.rent.dto.KostRentRequest;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;

public interface KostRentService {
    List<KostRent> findAll();

    List<KostRent> findAllByTenantId(Integer id);

    KostRent findById(Integer id);

    KostRent create(KostRentRequest request);

    void delete(Integer id);

}

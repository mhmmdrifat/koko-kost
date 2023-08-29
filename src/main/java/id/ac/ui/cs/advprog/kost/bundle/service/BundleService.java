package id.ac.ui.cs.advprog.kost.bundle.service;

import java.util.List;

import id.ac.ui.cs.advprog.kost.bundle.dto.BundleRequest;
import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;

public interface BundleService {
    List<Bundle> findAll();

    Bundle findById(Integer id);

    Bundle create(BundleRequest request);

    void delete(Integer id);

}

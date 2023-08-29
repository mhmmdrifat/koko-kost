package id.ac.ui.cs.advprog.kost.teman_menginap.service;

import id.ac.ui.cs.advprog.kost.teman_menginap.dto.CreateTemanMenginapRequest;
import id.ac.ui.cs.advprog.kost.teman_menginap.model.TemanMenginap;

import java.util.List;

public interface TemanMenginapService {
    void delete(Integer id);

    List<TemanMenginap> findAll();

    List<TemanMenginap> findAllByTenantId(Integer userId);

    TemanMenginap findById(Integer id);

    TemanMenginap create(CreateTemanMenginapRequest request);

}

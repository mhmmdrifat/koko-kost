package id.ac.ui.cs.advprog.kost.rental.service;

import id.ac.ui.cs.advprog.kost.rental.dto.Rental;
import java.util.List;

public interface RentalService {
    public List<Rental> findAllByUserId(Integer id);
}

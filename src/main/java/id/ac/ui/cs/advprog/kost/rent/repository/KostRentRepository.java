package id.ac.ui.cs.advprog.kost.rent.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.kost.rent.model.KostRent;

@Repository
public interface KostRentRepository extends JpaRepository<KostRent, Integer> {

    @NonNull
    List<KostRent> findAll();

    @NonNull
    Optional<KostRent> findById(@NonNull Integer id);

    void deleteById(@NonNull Integer id);
}

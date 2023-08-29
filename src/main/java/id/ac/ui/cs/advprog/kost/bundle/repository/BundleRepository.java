package id.ac.ui.cs.advprog.kost.bundle.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, Integer> {

    @NonNull
    List<Bundle> findAll();

    @NonNull
    Optional<Bundle> findById(@NonNull Integer id);

    void deleteById(@NonNull Integer id);
}

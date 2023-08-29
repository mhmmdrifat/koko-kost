package id.ac.ui.cs.advprog.kost.teman_menginap.repository;
import id.ac.ui.cs.advprog.kost.teman_menginap.model.TemanMenginap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemanMenginapRepository extends JpaRepository<TemanMenginap, Integer> {
    @NonNull
    List<TemanMenginap> findAll();

    @NonNull
    Optional<TemanMenginap> findById(@NonNull Integer id);

}

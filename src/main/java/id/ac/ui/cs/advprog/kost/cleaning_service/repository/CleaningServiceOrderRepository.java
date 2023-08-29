package id.ac.ui.cs.advprog.kost.cleaning_service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;


public interface CleaningServiceOrderRepository extends JpaRepository<CleaningServiceOrder,Integer> {

    @NonNull
    List<CleaningServiceOrder> findAll();

    @NonNull
    Optional<CleaningServiceOrder> findById(@NonNull Integer id);

    void deleteById(@NonNull Integer id);

}

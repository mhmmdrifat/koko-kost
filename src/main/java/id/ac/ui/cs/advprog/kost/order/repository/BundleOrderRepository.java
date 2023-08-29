package id.ac.ui.cs.advprog.kost.order.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.kost.order.model.BundleOrder;

@Repository
public interface BundleOrderRepository extends JpaRepository<BundleOrder, Integer> {

    @NonNull
    List<BundleOrder> findAll();

    @NonNull
    Optional<BundleOrder> findById(@NonNull Integer id);

    void deleteById(@NonNull Integer id);
}

package id.ac.ui.cs.advprog.kost.room.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.kost.room.model.KostRoom;

@Repository
public interface KostRoomRepository extends JpaRepository<KostRoom, Integer> {

    @NonNull
    List<KostRoom> findAll();

    @NonNull
    Optional<KostRoom> findById(@NonNull Integer id);

    @NonNull
    Optional<KostRoom> findByNameIgnoreCase(@NonNull String roomName);

    void deleteById(@NonNull Integer id);
}

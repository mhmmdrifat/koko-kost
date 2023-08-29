package id.ac.ui.cs.advprog.kost.room.service;

import java.util.List;

import id.ac.ui.cs.advprog.kost.room.dto.KostRoomRequest;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;

public interface KostRoomService {
    List<KostRoom> findAll();

    KostRoom findById(Integer id);

    KostRoom create(KostRoomRequest request);

    KostRoom update(Integer id, KostRoomRequest request);

    void delete(Integer id);

}

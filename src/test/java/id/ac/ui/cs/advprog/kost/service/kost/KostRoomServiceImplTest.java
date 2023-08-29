package id.ac.ui.cs.advprog.kost.service.kost;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.kost.room.dto.KostRoomRequest;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;
import id.ac.ui.cs.advprog.kost.room.service.KostRoomServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KostRoomServiceImplTest {

    @InjectMocks
    private KostRoomServiceImpl service;

    @Mock
    private KostRoomRepository repository;

    KostRoom room;
    KostRoom newRoom;
    KostRoomRequest createRequest;
    KostRoomRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = KostRoomRequest.builder()
                .name("Campingski Resort")
                .type("CAMPUR")
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(2)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        updateRequest = KostRoomRequest.builder()
                .name("Campingski Resort")
                .type("PUTRI")
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(2)
                .price((double) 1200000)
                .discount(15)
                .minDiscountDuration(15)
                .build();

        room = KostRoom.builder()
                .id(0)
                .name("Campingski Resort")
                .type(KostRoomType.CAMPUR)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(2)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        newRoom = KostRoom.builder()
                .id(0)
                .name("Campingski Resort")
                .type(KostRoomType.PUTRI)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(2)
                .price((double) 1200000)
                .discount(15)
                .minDiscountDuration(15)
                .build();
    }

    @Test
    void whenFindAllKostRoomShouldReturnListOfKostRooms() {
        List<KostRoom> allRooms = List.of(room);

        when(repository.findAll()).thenReturn(allRooms);

        List<KostRoom> result = service.findAll();
        verify(repository, atLeastOnce()).findAll();
        Assertions.assertEquals(allRooms, result);
    }

    @Test
    void whenFindByIdAndFoundShouldReturnKostRoom() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(room));

        KostRoom result = service.findById(0);
        verify(repository, atLeastOnce()).findById(any(Integer.class));
        Assertions.assertEquals(room, result);
    }

    @Test
    void whenFindByIdAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(KostRoomDoesNotExistException.class, () -> {
            service.findById(0);
        });
    }

    @Test
    void whenCreateKostRoomShouldReturnTheCreatedKostRoom() {
        when(repository.save(any(KostRoom.class))).thenAnswer(invocation -> {
            var room = invocation.getArgument(0, KostRoom.class);
            room.setId(0);
            return room;
        });

        KostRoom result = service.create(createRequest);
        verify(repository, atLeastOnce()).save(any(KostRoom.class));
        Assertions.assertEquals(room, result);
    }

    @Test
    void whenUpdateKostRoomAndFoundShouldReturnTheUpdatedKostRoom() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(room));
        when(repository.save(any(KostRoom.class))).thenAnswer(invocation ->
                invocation.getArgument(0, KostRoom.class));

        KostRoom result = service.update(0, updateRequest);
        verify(repository, atLeastOnce()).save(any(KostRoom.class));
        Assertions.assertEquals(newRoom, result);
    }

    @Test
    void whenUpdateKostRoomAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(KostRoomDoesNotExistException.class, () -> {
            service.update(0, createRequest);
        });
    }

    @Test
    void whenDeleteKostRoomAndFoundShouldCallDeleteByIdOnRepo() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(room));

        service.delete(0);
        verify(repository, atLeastOnce()).deleteById(any(Integer.class));
    }

    @Test
    void whenDeleteKostRoomAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(KostRoomDoesNotExistException.class, () -> {
            service.delete(0);
        });
    }

}
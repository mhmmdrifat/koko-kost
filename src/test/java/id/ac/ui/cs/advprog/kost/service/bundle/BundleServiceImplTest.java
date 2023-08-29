package id.ac.ui.cs.advprog.kost.service.bundle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.kost.bundle.dto.BundleRequest;
import id.ac.ui.cs.advprog.kost.bundle.exceptions.BundleDoesNotExistException;
import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import id.ac.ui.cs.advprog.kost.bundle.repository.BundleRepository;
import id.ac.ui.cs.advprog.kost.bundle.service.BundleServiceImpl;
import id.ac.ui.cs.advprog.kost.room.exceptions.KostRoomDoesNotExistException;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.room.repository.KostRoomRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BundleServiceImplTest {

    @InjectMocks
    private BundleServiceImpl service;

    @Mock
    private BundleRepository repository;
    @Mock
    private KostRoomRepository roomRepository;

    @Mock
    KostRoom room;
    Bundle bundle;
    Bundle newBundle;
    BundleRequest createRequest;
    BundleRequest updateRequest;

    @BeforeEach
    void setUp() {
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

        createRequest = BundleRequest.builder()
                .name("Campingski Bundle")
                .kostRoomId(0)
                .coworkingId(0)
                .duration(18)
                .bundlePrice((double) 11_000_000)
                .build();

        bundle = Bundle.builder()
                .id(0)
                .name("Campingski Bundle")
                .kostRoom(room)
                .coworkingId(0)
                .duration(18)
                .bundlePrice((double) 11_000_000)
                .build();

    }

    @Test
    void whenFindAllBundleShouldReturnListOfBundles() {
        List<Bundle> allBundles = List.of(bundle);

        when(repository.findAll()).thenReturn(allBundles);

        List<Bundle> result = service.findAll();
        verify(repository, atLeastOnce()).findAll();
        Assertions.assertEquals(allBundles, result);
    }

    @Test
    void whenFindByIdAndFoundShouldReturnBundle() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(bundle));

        Bundle result = service.findById(0);
        verify(repository, atLeastOnce()).findById(any(Integer.class));
        Assertions.assertEquals(bundle, result);
    }

    @Test
    void whenFindByIdAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(BundleDoesNotExistException.class, () -> {
            service.findById(0);
        });
    }

    @Test
    void whenDeleteBundleAndFoundShouldCallDeleteByIdOnRepo() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(bundle));

        service.delete(0);
        verify(repository, atLeastOnce()).deleteById(any(Integer.class));
    }

    @Test
    void whenDeleteBundleAndNotFoundShouldThrowException() {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(BundleDoesNotExistException.class, () -> {
            service.delete(0);
        });
    }

    @Test
    void whenCreateBundleShouldReturnCreatedBundle() {
        when(roomRepository.findById(anyInt())).thenReturn(Optional.of(room));
        when(repository.save(any(Bundle.class))).thenReturn(bundle);

        Bundle result = service.create(createRequest);

        verify(roomRepository, atLeastOnce()).findById(anyInt());
        verify(repository, atLeastOnce()).save(any(Bundle.class));

        Assertions.assertEquals(bundle, result);
    }

@Test
    void whenCreateBundleAndRoomNotFoundShouldThrowException() {
        when(roomRepository.findById(anyInt())).thenReturn(Optional.empty());

        Assertions.assertThrows(KostRoomDoesNotExistException.class, () -> {
            service.create(createRequest);
        });

        verify(roomRepository, atLeastOnce()).findById(anyInt());
        verify(repository, never()).save(any(Bundle.class));
    }

}
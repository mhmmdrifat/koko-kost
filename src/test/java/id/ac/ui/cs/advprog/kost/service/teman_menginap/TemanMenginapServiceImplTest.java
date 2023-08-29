package id.ac.ui.cs.advprog.kost.service.teman_menginap;

import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.rent.exceptions.InvalidTenantException;
import id.ac.ui.cs.advprog.kost.teman_menginap.dto.CreateTemanMenginapRequest;
import id.ac.ui.cs.advprog.kost.teman_menginap.exceptions.InvalidEmailException;
import id.ac.ui.cs.advprog.kost.teman_menginap.exceptions.InvalidTemanMenginapTenantException;
import id.ac.ui.cs.advprog.kost.teman_menginap.exceptions.TemanMenginapDoesNotExistException;
import id.ac.ui.cs.advprog.kost.teman_menginap.model.TemanMenginap;
import id.ac.ui.cs.advprog.kost.teman_menginap.repository.TemanMenginapRepository;
import id.ac.ui.cs.advprog.kost.teman_menginap.service.TemanMenginapServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemanMenginapServiceImplTest {
    @Mock
    private TemanMenginapRepository temanMenginapRepository;

    @InjectMocks
    private TemanMenginapServiceImpl temanMenginapService;

    List<GrantedAuthority> authorities = new ArrayList<>();

    private TemanMenginap temanMenginap;
    private CreateTemanMenginapRequest createRequest;

    @BeforeEach
    void setUp() {

        authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(1, "PELANGGAN", "agun", true, (double) 2_000_000),
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        temanMenginap = TemanMenginap.builder()
                .id(1)
                .userId(1)
                .name("Test Name")
                .email("test@email.com")
                .reason("Test Reason")
                .bookingStatus("Test Status")
                .build();

        createRequest = CreateTemanMenginapRequest.builder()
                .userId(1)
                .name("Test Name")
                .email("test@email.com")
                .reason("Test Reason")
                .bookingStatus("Test Status")
                .build();
    }

    @Test
    void testCreateTemanMenginap() {
        when(temanMenginapRepository.save(any(TemanMenginap.class))).thenReturn(temanMenginap);

        TemanMenginap createdTemanMenginap = temanMenginapService.create(createRequest);
        verify(temanMenginapRepository, atLeastOnce()).save(any(TemanMenginap.class));
        assertEquals(temanMenginap.getUserId(), createdTemanMenginap.getUserId());
        assertEquals(temanMenginap.getBookingStatus(), createdTemanMenginap.getBookingStatus());
        assertEquals(temanMenginap.getName(), createdTemanMenginap.getName());
        assertEquals(temanMenginap.getReason(), createdTemanMenginap.getReason());
    }

    @Test
    void testFindByIdExistingTemanMenginap() {
        when(temanMenginapRepository.findById(any(Integer.class))).thenReturn(Optional.of(temanMenginap));

        TemanMenginap foundTemanMenginap = temanMenginapService.findById(1);

        assertEquals(temanMenginap.getId(), foundTemanMenginap.getId());
    }

    @Test
    void testFindByIdNonExistingTemanMenginap() {
        when(temanMenginapRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(TemanMenginapDoesNotExistException.class, () -> temanMenginapService.findById(1));
    }

    @Test
    void testFindAllTemanMenginap() {
        List<TemanMenginap> temanMenginapList = new ArrayList<>();
        temanMenginapList.add(temanMenginap);

        when(temanMenginapRepository.findAll()).thenReturn(temanMenginapList);

        List<TemanMenginap> foundTemanMenginap = temanMenginapService.findAll();

        assertFalse(foundTemanMenginap.isEmpty());
        assertEquals(1, foundTemanMenginap.size());
    }

    @Test
    void testFindAllByTenantId() {
        List<TemanMenginap> temanMenginapList = new ArrayList<>();
        temanMenginapList.add(temanMenginap);

        when(temanMenginapRepository.findAll()).thenReturn(temanMenginapList);

        List<TemanMenginap> foundTemanMenginap = temanMenginapService.findAllByTenantId(1);

        assertFalse(foundTemanMenginap.isEmpty());
        assertEquals(1, foundTemanMenginap.size());
        assertEquals(1, foundTemanMenginap.get(0).getUserId());
    }

    @Test
    void testDeleteExistingTemanMenginap() {
        when(temanMenginapRepository.findById(any(Integer.class))).thenReturn(Optional.of(temanMenginap));

        assertDoesNotThrow(() -> temanMenginapService.delete(1));
    }

    @Test
    void testDeleteNonExistingTemanMenginap() {
        when(temanMenginapRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        assertThrows(TemanMenginapDoesNotExistException.class, () -> temanMenginapService.delete(1));
    }

    @Test
    void testCreateTemanMenginapWithInvalidUserId() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(null, "PELANGGAN", "agun", true, (double) 2_000_000),
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        assertThrows(InvalidTenantException.class, () -> temanMenginapService.create(createRequest));
    }

    @Test
    void testFindByIdInvalidTenant() {
        when(temanMenginapRepository.findById(any(Integer.class))).thenReturn(Optional.of(temanMenginap));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(2, "PELANGGAN", "agun", true, (double) 2_000_000),
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        assertThrows(InvalidTemanMenginapTenantException.class, () -> temanMenginapService.findById(1));
    }

    @Test
    void testCreateTemanMenginapWithUnmatchingUserId() {
        CreateTemanMenginapRequest unmatchedRequest = CreateTemanMenginapRequest.builder()
                .userId(2) // UserId that does not match the one in Authentication
                .name("Test Name")
                .email("test@email.com")
                .reason("Test Reason")
                .bookingStatus("Test Status")
                .build();

        assertThrows(InvalidTenantException.class, () -> temanMenginapService.create(unmatchedRequest));
    }

    @Test
    void testCreateTemanMenginapWithInvalidEmail() {
        CreateTemanMenginapRequest invalidEmailRequest = CreateTemanMenginapRequest.builder()
                .userId(1)
                .name("Test Name")
                .email("invalidEmail") // Invalid email
                .reason("Test Reason")
                .bookingStatus("Test Status")
                .build();

        assertThrows(InvalidEmailException.class, () -> temanMenginapService.create(invalidEmailRequest));
    }


}


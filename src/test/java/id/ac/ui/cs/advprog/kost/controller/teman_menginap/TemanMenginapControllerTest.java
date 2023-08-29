package id.ac.ui.cs.advprog.kost.controller.teman_menginap;

// ...other imports...
import id.ac.ui.cs.advprog.kost.Util;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.core.service.JwtService;
import id.ac.ui.cs.advprog.kost.teman_menginap.controller.TemanMenginapController;
import id.ac.ui.cs.advprog.kost.teman_menginap.dto.CreateTemanMenginapRequest;
import id.ac.ui.cs.advprog.kost.teman_menginap.model.TemanMenginap;
import id.ac.ui.cs.advprog.kost.teman_menginap.service.TemanMenginapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = TemanMenginapController.class)
@AutoConfigureMockMvc
class TemanMenginapControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TemanMenginapService temanMenginapService;

    @MockBean
    private JwtService jwtService;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    private TemanMenginap temanMenginap;
    private CreateTemanMenginapRequest createRequest;

    @BeforeEach
    void setUp() {
        authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(1, "PENGELOLA", "agun", true, (double) 2_000_000),
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

    // ...setUp method and other test data initialization...

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testGetAllDaftarRiwayat() throws Exception {
        when(temanMenginapService.findAll()).thenReturn(Collections.singletonList(temanMenginap));

        mvc.perform(get("/kost/temanMenginap/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllDaftarRiwayat"))
                .andExpect(jsonPath("$[0].id").value(temanMenginap.getId()));

        verify(temanMenginapService, atLeastOnce()).findAll();
    }

    @Test
    @WithMockUser(authorities = "PELANGGAN")
    void testCreateTemanMenginap() throws Exception {
        when(temanMenginapService.create(any(CreateTemanMenginapRequest.class))).thenReturn(temanMenginap);

        mvc.perform(post("/kost/temanMenginap/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Util.mapToJson(createRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(handler().methodName("createTemanMenginap"))
                .andExpect(jsonPath("$.id").value(temanMenginap.getId()));

        verify(temanMenginapService, atLeastOnce()).create(any(CreateTemanMenginapRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PELANGGAN")
    void testGetMyDaftarRiwayat() throws Exception {
        when(temanMenginapService.findAllByTenantId(anyInt())).thenReturn(Collections.singletonList(temanMenginap));

        mvc.perform(get("/kost/temanMenginap/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getMyDaftarRiwayat"))
                .andExpect(jsonPath("$[0].id").value(temanMenginap.getId()));

        verify(temanMenginapService, atLeastOnce()).findAllByTenantId(anyInt());
    }

    @Test
    @WithMockUser(authorities = {"PENGELOLA", "PELANGGAN"})
    void testGetDaftarRiwayatById() throws Exception {
        when(temanMenginapService.findById(any(Integer.class))).thenReturn(temanMenginap);

        mvc.perform(get("/kost/temanMenginap/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getDaftarRiwayatById"))
                .andExpect(jsonPath("$.id").value(temanMenginap.getId()));

        verify(temanMenginapService, atLeastOnce()).findById(any(Integer.class));
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testDeleteTemanMenginap() throws Exception {
        mvc.perform(delete("/kost/temanMenginap/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("deleteTemanMenginap"));

        verify(temanMenginapService, atLeastOnce()).delete(any(Integer.class));
    }

}

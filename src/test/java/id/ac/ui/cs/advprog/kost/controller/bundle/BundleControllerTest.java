package id.ac.ui.cs.advprog.kost.controller.bundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import id.ac.ui.cs.advprog.kost.Util;
import id.ac.ui.cs.advprog.kost.bundle.controller.BundleController;
import id.ac.ui.cs.advprog.kost.bundle.dto.BundleRequest;
import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import id.ac.ui.cs.advprog.kost.bundle.service.BundleServiceImpl;
import id.ac.ui.cs.advprog.kost.core.service.JwtService;

import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BundleController.class)
@AutoConfigureMockMvc
class BundleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BundleServiceImpl service;

    @MockBean
    private JwtService jwtService;

    Bundle bundle;
    KostRoom room;
    BundleRequest bodyContent;

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

        bundle = Bundle.builder()
                .id(1)
                .name("Campingski Bundle")
                .kostRoom(room)
                .coworkingId(0)
                .duration(18)
                .bundlePrice((double) 11_000_000)
                .build();

        bodyContent = BundleRequest.builder()
                .name("Campingski Bundle")
                .kostRoomId(0)
                .coworkingId(0)
                .duration(18)
                .bundlePrice((double) 11_000_000)
                .build();

    }

    @Test
    @WithMockUser()
    void testGetAllBundles() throws Exception {
        List<Bundle> allBundles = List.of(bundle);

        when(service.findAll()).thenReturn(allBundles);

        mvc.perform(get("/bundle/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllBundle"))
                .andExpect(jsonPath("$[0].name").value(bundle.getName()));

        verify(service, atLeastOnce()).findAll();
    }

    @Test
    @WithMockUser()
    void testGetBundleById() throws Exception {
        when(service.findById(any(Integer.class))).thenReturn(bundle);

        mvc.perform(get("/bundle/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getBundleById"))
                .andExpect(jsonPath("$.name").value(bundle.getName()));

        verify(service, atLeastOnce()).findById(any(Integer.class));
    }

    @Test
    @WithMockUser(authorities  = "PENGELOLA")
    void testAddBundle() throws Exception {
        when(service.create(any(BundleRequest.class))).thenReturn(bundle);

        mvc.perform(post("/bundle/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Util.mapToJson(bodyContent))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(handler().methodName("addBundle"))
                .andExpect(jsonPath("$.name").value(bundle.getName()));

        verify(service, atLeastOnce()).create(any(BundleRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testDeleteBundle() throws Exception {
        mvc.perform(delete("/bundle/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("deleteBundle"));

        verify(service, atLeastOnce()).delete(any(Integer.class));
    }

}

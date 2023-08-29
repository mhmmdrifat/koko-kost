package id.ac.ui.cs.advprog.kost.controller.kost;

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
import id.ac.ui.cs.advprog.kost.core.service.JwtService;
import id.ac.ui.cs.advprog.kost.room.controller.KostRoomController;
import id.ac.ui.cs.advprog.kost.room.dto.KostRoomRequest;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import id.ac.ui.cs.advprog.kost.room.service.KostRoomServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KostRoomController.class)
@AutoConfigureMockMvc
class KostRoomControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private KostRoomServiceImpl service;

    @MockBean
    private JwtService jwtService;

    KostRoom room;
    KostRoomRequest bodyContent;

    @BeforeEach
    void setUp() {
        room = KostRoom.builder()
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

        bodyContent = KostRoomRequest.builder()
                .name("Test Room")
                .type("CAMPUR")
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl jl")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(2)
                .price((double) 12000000)
                .discount(2)
                .minDiscountDuration(12)
                .build();

    }

    @Test
    @WithMockUser()
    void testGetAllRooms() throws Exception {
        List<KostRoom> allRooms = List.of(room);

        when(service.findAll()).thenReturn(allRooms);

        mvc.perform(get("/kost/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllKostRoom"))
                .andExpect(jsonPath("$[0].name").value(room.getName()));

        verify(service, atLeastOnce()).findAll();
    }

    @Test
    @WithMockUser()
    void testGetKostRoomById() throws Exception {
        when(service.findById(any(Integer.class))).thenReturn(room);

        mvc.perform(get("/kost/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getKostRoomById"))
                .andExpect(jsonPath("$.name").value(room.getName()));

        verify(service, atLeastOnce()).findById(any(Integer.class));
    }

    @Test
    @WithMockUser(authorities  = "PENGELOLA")
    void testAddKostRoom() throws Exception {
        when(service.create(any(KostRoomRequest.class))).thenReturn(room);

        mvc.perform(post("/kost/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Util.mapToJson(bodyContent))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(handler().methodName("addKostRoom"))
                .andExpect(jsonPath("$.name").value(room.getName()));

        verify(service, atLeastOnce()).create(any(KostRoomRequest.class));
    }

    @Test
    @WithMockUser(authorities  = "PENGELOLA")
    void testPutKostRoom() throws Exception {
        when(service.update(any(Integer.class), any(KostRoomRequest.class))).thenReturn(room);

        mvc.perform(patch("/kost/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Util.mapToJson(bodyContent))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("putKostRoom"))
                .andExpect(jsonPath("$.name").value(room.getName()));

        verify(service, atLeastOnce()).update(any(Integer.class), any(KostRoomRequest.class));
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testDeleteRoom() throws Exception {
        mvc.perform(delete("/kost/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("deleteKostRoom"));

        verify(service, atLeastOnce()).delete(any(Integer.class));
    }

}

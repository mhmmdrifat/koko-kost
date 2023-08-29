package id.ac.ui.cs.advprog.kost.controller.kost;

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

import id.ac.ui.cs.advprog.kost.Util;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.core.service.JwtService;
import id.ac.ui.cs.advprog.kost.rent.controller.KostRentController;
import id.ac.ui.cs.advprog.kost.rent.dto.KostRentRequest;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.rent.service.KostRentServiceImpl;

import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KostRentController.class)
@AutoConfigureMockMvc
class KostRentControllerTest {

        @Autowired
        private MockMvc mvc;

        @MockBean
        private KostRentServiceImpl service;

        @MockBean
        private JwtService jwtService;

        List<GrantedAuthority> authorities = new ArrayList<>();

        KostRoom room;
        KostRent rent;
        KostRentRequest bodyContent;

        @BeforeEach
        void setUp() {
                authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                null,
                                new JwtPayload(1, "PENGELOLA", "agun", true, (double) 2_000_000),
                                authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                room = KostRoom.builder()
                                .id(1)
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

                rent = KostRent.builder()
                                .id(1)
                                .userId(1)
                                .userName("agun")
                                .kostRoom(room)
                                .checkInDate(Util.parseDate("2014-05-01"))
                                .checkOutDate(Util.parseDate("2014-08-01"))
                                .duration(3)
                                .totalPrice((double) 3600000)
                                .build();

                bodyContent = KostRentRequest
                                .builder()
                                .userId(1)
                                .kostRoomId(1)
                                .checkInDate("2023-05-01")
                                .checkOutDate("2023-08-01")
                                .duration(3)
                                .totalPrice((double) 36000000)
                                .build();

        }

        @Test
        @WithMockUser()
        void testGetAllRents() throws Exception {
                List<KostRent> allRents = List.of(rent);

                when(service.findAll()).thenReturn(allRents);

                mvc.perform(get("/kost/rent/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("getAllKostRent"))
                                .andExpect(jsonPath("$[0].id").value(rent.getId()));

                verify(service, atLeastOnce()).findAll();
        }

    @Test
    @WithMockUser()
    void testGetKostRentById() throws Exception {
        when(service.findById(any(Integer.class))).thenReturn(rent);

        mvc.perform(get("/kost/rent/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getKostRentById"))
                .andExpect(jsonPath("$.id").value(rent.getId()));

        verify(service, atLeastOnce()).findById(any(Integer.class));
    }

        @Test
        @WithMockUser(authorities = "PELANGGAN")
        void testAddKostRent() throws Exception {
                
                when(service.create(any(KostRentRequest.class))).thenReturn(rent);
                System.out.println(Util.mapToJson(bodyContent));

                mvc.perform(post("/kost/rent/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Util.mapToJson(bodyContent))
                                .with(csrf()))
                                .andExpect(status().isCreated())
                                .andExpect(handler().methodName("addKostRent"))
                                .andExpect(jsonPath("$.id").value(rent.getId()));

                verify(service, atLeastOnce()).create(any(KostRentRequest.class));
        }

        @Test
        @WithMockUser(authorities = "PELANGGAN")
        void testAddKostRentWithInvalidRequest() throws Exception {
                bodyContent.setCheckInDate(null);
                bodyContent.setKostRoomId(-1);
                System.out.println(Util.mapToJson(bodyContent));

                mvc.perform(post("/kost/rent/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Util.mapToJson(bodyContent))
                                .with(csrf()))
                                .andExpect(status().isBadRequest());

        }

        @Test
        @WithMockUser(authorities = "PELANGGAN")
        void testGetMyKostRent() throws Exception {
                List<KostRent> allRents = List.of(rent);
                when(service.findAllByTenantId(any(Integer.class))).thenReturn(allRents);

                mvc.perform(get("/kost/rent/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("getMyKostRent"));

        }

        @Test
        @WithMockUser(authorities = "PENGELOLA")
        void testDeleteRent() throws Exception {
                mvc.perform(delete("/kost/rent/delete/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("deleteKostRent"));

                verify(service, atLeastOnce()).delete(any(Integer.class));
        }

}

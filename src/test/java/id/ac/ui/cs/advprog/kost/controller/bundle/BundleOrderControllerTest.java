package id.ac.ui.cs.advprog.kost.controller.bundle;

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

import id.ac.ui.cs.advprog.kost.bundle.model.Bundle;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.core.service.JwtService;
import id.ac.ui.cs.advprog.kost.order.controller.BundleOrderController;
import id.ac.ui.cs.advprog.kost.order.dto.BundleOrderRequest;
import id.ac.ui.cs.advprog.kost.order.model.BundleOrder;
import id.ac.ui.cs.advprog.kost.order.service.BundleOrderServiceImpl;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BundleOrderController.class)
@AutoConfigureMockMvc
class BundleOrderControllerTest {

        @Autowired
        private MockMvc mvc;

        @MockBean
        private BundleOrderServiceImpl service;

        @MockBean
        private JwtService jwtService;

        List<GrantedAuthority> authorities = new ArrayList<>();

        Bundle bundle;
        BundleOrder order;
        KostRoom room;

        BundleOrderRequest bodyContent;

        @BeforeEach
        void setUp() {
                authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                null,
                                new JwtPayload(0, "PELANGGAN", "agun", true, (double) 2_000_000),
                                authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

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
                                .name("Campingski BundleOrder")
                                .kostRoom(room)
                                .coworkingId(0)
                                .duration(18)
                                .bundlePrice((double) 11_000_000)
                                .build();

                order = BundleOrder.builder()
                                .id(1)
                                .userId(0)
                                .bundle(bundle)
                                .checkInDate(Util.parseDate("2023-01-01"))
                                .checkOutDate(Util.parseDate("2023-06-01"))
                                .build();

                bodyContent = BundleOrderRequest.builder()
                                .bundleId(1)
                                .checkInDate(Util.parseDate("2023-01-01"))
                                .checkOutDate(Util.parseDate("2023-06-01"))
                                .build();

        }

        @Test
        @WithMockUser()
        void testGetAllBundleOrders() throws Exception {
                List<BundleOrder> allOrders = List.of(order);

                when(service.findAll()).thenReturn(allOrders);

                mvc.perform(get("/bundle/order/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("getAllBundleOrder"))
                                .andExpect(jsonPath("$[0].id").value(order.getId()));

                verify(service, atLeastOnce()).findAll();
        }

    @Test
    @WithMockUser()
    void testGetBundleOrderById() throws Exception {
        when(service.findById(any(Integer.class))).thenReturn(order);

        mvc.perform(get("/bundle/order/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getBundleOrderById"))
                .andExpect(jsonPath("$.id").value(order.getId()));

        verify(service, atLeastOnce()).findById(any(Integer.class));
    }

        @Test
        @WithMockUser(authorities = "PELANGGAN")
        void testGetAllBundleOrdersByUserId() throws Exception {
                List<BundleOrder> allOrders = List.of(order);
                when(service.findAllByTenantId(any(Integer.class))).thenReturn(allOrders);

                mvc.perform(get("/bundle/order/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("getMyBundleOrder"))
                                .andExpect(jsonPath("$[0].id").value(order.getId()));

                verify(service, atLeastOnce()).findAllByTenantId(any(Integer.class));
        }

    @Test
    @WithMockUser(authorities  = "PELANGGAN")
    void testAddBundleOrder() throws Exception {
        when(service.create(any(BundleOrderRequest.class))).thenReturn(order);
      
        mvc.perform(post("/bundle/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Util.mapToJson(bodyContent))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(handler().methodName("addBundleOrder"))
                .andExpect(jsonPath("$.id").value(order.getId()));

        verify(service, atLeastOnce()).create(any(BundleOrderRequest.class));
    }

        @Test
        @WithMockUser(authorities = "PELANGGAN")
        void testGetMyBundleOrder() throws Exception {
                List<BundleOrder> allOrders = List.of(order);
                when(service.findAllByTenantId(any(Integer.class))).thenReturn(allOrders);

                mvc.perform(get("/bundle/order/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("getMyBundleOrder"));

        }

        @Test
        @WithMockUser(authorities = "PENGELOLA")
        void testDeleteBundle() throws Exception {
                mvc.perform(delete("/bundle/order/delete/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(handler().methodName("deleteBundleOrder"));

                verify(service, atLeastOnce()).delete(any(Integer.class));
        }

}

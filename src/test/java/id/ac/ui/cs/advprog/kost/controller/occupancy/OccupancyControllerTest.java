package id.ac.ui.cs.advprog.kost.controller.occupancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.kost.Util;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceOrder;
import id.ac.ui.cs.advprog.kost.cleaning_service.model.CleaningServiceStatus;
import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.core.service.JwtService;
import id.ac.ui.cs.advprog.kost.occupancy.controller.OccupancyController;
import id.ac.ui.cs.advprog.kost.occupancy.service.OccupancyServiceImpl;
import id.ac.ui.cs.advprog.kost.rent.model.KostRent;
import id.ac.ui.cs.advprog.kost.room.model.KostRoom;
import id.ac.ui.cs.advprog.kost.room.model.KostRoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import id.ac.ui.cs.advprog.kost.occupancy.dto.Tenant;
import id.ac.ui.cs.advprog.kost.occupancy.dto.WithoutTenant;
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
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = OccupancyController.class)
@AutoConfigureMockMvc
class OccupancyControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OccupancyServiceImpl occupancyService;
    @MockBean
    private JwtService jwtService;
    List<GrantedAuthority> authorities = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    KostRoom kostRoom1;
    KostRoom kostRoom2;
    KostRent kostRent1;
    KostRent kostRent2;
    CleaningServiceOrder roomService1;
    CleaningServiceOrder roomService2;

    @BeforeEach
    void setUp() {
        authorities.add(new SimpleGrantedAuthority("PENGELOLA"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                null,
                new JwtPayload(5, "PENGELOLA", "Mark", true, (double) 2_000_000),
                authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        kostRoom1 = KostRoom.builder()
                .id(0)
                .name("Campingski Resort")
                .type(KostRoomType.CAMPUR)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-4.jpg" })
                .stock(3)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        kostRoom2 = KostRoom.builder()
                .id(1)
                .name("Superski Resort")
                .type(KostRoomType.CAMPUR)
                .city("Jakarta")
                .country("Indonesia")
                .address("Jl Kebenaran")
                .facilities(new String[] { "Kamar mandi", "AC" })
                .images(new String[] {
                        "https://res.cloudinary.com/dkg0oswii/image/upload/v1669102647/cld-sample-5.jpg" })
                .stock(5)
                .price((double) 1200000)
                .discount(12)
                .minDiscountDuration(12)
                .build();

        kostRent1 = KostRent.builder()
                .id(0)
                .userId(0)
                .userName("Agun")
                .roomNumber(4)
                .kostRoom(kostRoom1)
                .checkInDate(Util.parseDate("2023-03-11"))
                .checkOutDate(Util.parseDate("2023-06-11"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        kostRent2 = KostRent.builder()
                .id(1)
                .userId(0)
                .userName("Agun")
                .roomNumber(5)
                .kostRoom(kostRoom1)
                .checkInDate(Util.parseDate("2023-04-10"))
                .checkOutDate(Util.parseDate("2023-06-10"))
                .duration(3)
                .totalPrice((double) 3600000)
                .build();

        roomService1 = CleaningServiceOrder.builder()
                .id(0)
                .UserId(0)
                .kostRent(kostRent1)
                .startDate(Util.parseDate("2023-05-05"))
                .endDate(Util.parseDate("2023-05-05"))
                .status(CleaningServiceStatus.FINISHED)
                .build();

        roomService2 = CleaningServiceOrder.builder()
                .id(1)
                .UserId(0)
                .kostRent(kostRent1)
                .startDate(Util.parseDate("2023-05-08"))
                .endDate(Util.parseDate("2023-05-08"))
                .status(CleaningServiceStatus.FINISHED)
                .build();
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testGetAllTenant() throws Exception {
        List<Tenant> tenants = Tenant.createTenants(List.of(kostRent1, kostRent2), List.of(roomService1, roomService2));

        when(occupancyService.findAllTenant()).thenReturn(tenants);

        MvcResult mvcResult = mvc.perform(get("/kost/tenants/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllTenant"))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(tenants), responseBody);

        verify(occupancyService, atLeastOnce()).findAllTenant();
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testGetAllTenantByRoomName() throws Exception {
        List<Tenant> tenants = Tenant.createTenants(List.of(kostRent1), List.of(roomService1, roomService2));

        when(occupancyService.findAllTenantByRoomName(any(String.class))).thenReturn(tenants);

        MvcResult mvcResult = mvc.perform(get("/kost/tenants/all/Campingski Resort")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllTenantByRoomName"))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(tenants), responseBody);

        verify(occupancyService, atLeastOnce()).findAllTenantByRoomName(any(String.class));
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testGetAllWithoutTenant() throws Exception {
        final int[] id = {0};
        List<WithoutTenant> withoutTenants = List.of(kostRoom1, kostRoom2).stream()
                .filter(KostRoom::getIsAvailable)
                .flatMap(availableRoom -> WithoutTenant.createWithoutTenants(availableRoom, id).stream())
                .toList();

        when(occupancyService.findAllWithoutTenant()).thenReturn(withoutTenants);

        MvcResult mvcResult = mvc.perform(get("/kost/without-tenants/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllWithoutTenant"))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(withoutTenants), responseBody);

        verify(occupancyService, atLeastOnce()).findAllWithoutTenant();
    }

    @Test
    @WithMockUser(authorities = "PENGELOLA")
    void testGetAllWithoutTenantByRoomName() throws Exception {
        List<WithoutTenant> withoutTenants = WithoutTenant.createWithoutTenants(kostRoom1, new int[]{0});

        when(occupancyService.findAllWithoutTenantByRoomName(any(String.class))).thenReturn(withoutTenants);

        MvcResult mvcResult = mvc.perform(get("/kost/without-tenants/all/Campingski Resort")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getAllWithoutTenantByRoomName"))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(withoutTenants), responseBody);

        verify(occupancyService, atLeastOnce()).findAllWithoutTenantByRoomName(any(String.class));
    }
}

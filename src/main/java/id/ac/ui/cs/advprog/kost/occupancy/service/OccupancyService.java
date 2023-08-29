package id.ac.ui.cs.advprog.kost.occupancy.service;

import id.ac.ui.cs.advprog.kost.occupancy.dto.Tenant;
import id.ac.ui.cs.advprog.kost.occupancy.dto.WithoutTenant;
import java.util.List;

public interface OccupancyService {
    List<Tenant> findAllTenant();
    List<Tenant> findAllTenantByRoomName(String roomName);
    List<WithoutTenant> findAllWithoutTenant();
    List<WithoutTenant> findAllWithoutTenantByRoomName(String roomName);
}

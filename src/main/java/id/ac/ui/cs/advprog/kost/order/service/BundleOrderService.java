package id.ac.ui.cs.advprog.kost.order.service;

import java.util.List;

import id.ac.ui.cs.advprog.kost.order.dto.BundleOrderRequest;
import id.ac.ui.cs.advprog.kost.order.model.BundleOrder;

public interface BundleOrderService {
    List<BundleOrder> findAll();

    List<BundleOrder> findAllByTenantId(Integer id);

    BundleOrder findById(Integer id);

    BundleOrder create(BundleOrderRequest request);

    void delete(Integer id);

}

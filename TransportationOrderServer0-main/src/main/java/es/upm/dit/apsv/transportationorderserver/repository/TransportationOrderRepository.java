package es.upm.dit.apsv.transportationorderserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.dit.apsv.transportationorderserver.model.TransportationOrder;

public interface TransportationOrderRepository extends JpaRepository<TransportationOrder, String> {

}

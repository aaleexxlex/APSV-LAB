package es.upm.dit.apsv.traceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.upm.dit.apsv.traceserver.model.Trace;

public interface TraceRepository extends JpaRepository<Trace, String> {

    List<Trace> findByTruck(String truck);

}

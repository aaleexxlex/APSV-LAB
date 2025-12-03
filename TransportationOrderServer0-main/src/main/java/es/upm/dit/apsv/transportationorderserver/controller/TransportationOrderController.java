package es.upm.dit.apsv.transportationorderserver.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.upm.dit.apsv.transportationorderserver.model.TransportationOrder;
import es.upm.dit.apsv.transportationorderserver.repository.TransportationOrderRepository;

@RestController
@RequestMapping("/orders")   // seguimos con /orders como habías hecho
public class TransportationOrderController {

    private final TransportationOrderRepository repo;

    public TransportationOrderController(TransportationOrderRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<TransportationOrder> all() {
        return repo.findAll();
    }

    @PostMapping
    public TransportationOrder newOrder(@RequestBody TransportationOrder o) {
        // si viene sin toid, aquí podrías generar uno, pero el JSON del profe lo trae
        return repo.save(o);
    }

    @GetMapping("/{id}")
    public TransportationOrder one(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found " + id));
    }

    @PutMapping("/{id}")
    public TransportationOrder replace(@RequestBody TransportationOrder o, @PathVariable String id) {
        return repo.findById(id)
                .map(existing -> {
                    // copiamos todos los campos del body a la entidad existente
                    existing.setTruck(o.getTruck());
                    existing.setOriginDate(o.getOriginDate());
                    existing.setOriginLat(o.getOriginLat());
                    existing.setOriginLong(o.getOriginLong());
                    existing.setDstDate(o.getDstDate());
                    existing.setDstLat(o.getDstLat());
                    existing.setDstLong(o.getDstLong());
                    existing.setLastDate(o.getLastDate());
                    existing.setLastLat(o.getLastLat());
                    existing.setLastLong(o.getLastLong());
                    existing.setSt(o.getSt());
                    return repo.save(existing);
                })
                .orElseGet(() -> {
                    // si no existe, creamos una nueva con ese id
                    o.setToid(id);
                    return repo.save(o);
                });
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repo.deleteById(id);
    }
}

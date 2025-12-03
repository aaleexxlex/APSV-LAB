package es.upm.dit.apsv.traceserver.controller;

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

import es.upm.dit.apsv.traceserver.model.Trace;
import es.upm.dit.apsv.traceserver.repository.TraceRepository;

@RestController
@RequestMapping("/traces")
public class TraceController {

    private final TraceRepository repo;

    public TraceController(TraceRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Trace> all() {
        return repo.findAll();
    }

    @PostMapping
    public Trace newTrace(@RequestBody Trace t) {
        return repo.save(t);
    }

    @GetMapping("/{id}")
    public Trace one(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(()
                        -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No trace with id " + id));
    }

    @GetMapping("/truck/{truck}")
    public List<Trace> byTruck(@PathVariable String truck) {
        return repo.findByTruck(truck);
    }

    @PutMapping("/{id}")
    public Trace replaceTrace(@RequestBody Trace newTrace, @PathVariable String id) {
        return repo.findById(id)
                .map(trace -> {
                    trace.setTruck(newTrace.getTruck());
                    trace.setLastSeen(newTrace.getLastSeen());
                    trace.setLat(newTrace.getLat());
                    trace.setLng(newTrace.getLng());
                    return repo.save(trace);
                })
                .orElseGet(() -> {
                    newTrace.setTraceId(id);
                    return repo.save(newTrace);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteTrace(@PathVariable String id) {
        repo.deleteById(id);
    }
}

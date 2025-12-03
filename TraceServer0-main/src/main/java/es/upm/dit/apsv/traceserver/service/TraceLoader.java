package es.upm.dit.apsv.traceserver.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.dit.apsv.traceserver.model.Trace;
import es.upm.dit.apsv.traceserver.repository.TraceRepository;

@Component
public class TraceLoader {

    private final TraceRepository repo;

    public TraceLoader(TraceRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        try {
            // Lee el fichero traces.json del classpath (src/main/resources)
            InputStream is = getClass().getResourceAsStream("/traces.json");
            if (is == null) {
                System.err.println("⚠ No se ha encontrado traces.json en resources");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Trace> traces = Arrays.asList(mapper.readValue(is, Trace[].class));

            repo.saveAll(traces);
            System.out.println("Cargadas " + traces.size() + " trazas desde traces.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

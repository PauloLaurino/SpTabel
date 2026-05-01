package br.com.spdealer.refatorado.masfor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/refatorado/masfor")
public class MasforController {

    private final MasforService service;

    public MasforController(MasforService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MasforDTO>> list(HttpSession session) {
        Integer idFil = (Integer) session.getAttribute("id_fil");
        if (idFil == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(service.listByFilial(idFil));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasforDTO> get(@PathVariable Long id, HttpSession session) {
        Integer idFil = (Integer) session.getAttribute("id_fil");
        if (idFil == null) return ResponseEntity.badRequest().build();
        MasforDTO dto = service.getById(id, idFil);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<MasforDTO> create(@Valid @RequestBody MasforDTO dto, HttpSession session) {
        Integer idFil = (Integer) session.getAttribute("id_fil");
        String user = (String) session.getAttribute("username");
        if (idFil == null) return ResponseEntity.badRequest().build();
        dto.setIdFil(idFil);
        MasforDTO created = service.create(dto, user == null ? "system" : user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasforDTO> update(@PathVariable Long id, @Valid @RequestBody MasforDTO dto, HttpSession session) {
        Integer idFil = (Integer) session.getAttribute("id_fil");
        String user = (String) session.getAttribute("username");
        if (idFil == null) return ResponseEntity.badRequest().build();
        MasforDTO updated = service.update(id, dto, idFil, user == null ? "system" : user);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        Integer idFil = (Integer) session.getAttribute("id_fil");
        if (idFil == null) return ResponseEntity.badRequest().build();
        boolean removed = service.delete(id, idFil);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

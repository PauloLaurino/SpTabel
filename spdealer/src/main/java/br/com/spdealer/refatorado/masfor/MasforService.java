package br.com.spdealer.refatorado.masfor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasforService {

    private final MasforRepository repository;

    public MasforService(MasforRepository repository) {
        this.repository = repository;
    }

    public List<MasforDTO> listByFilial(Integer idFil) {
        return repository.findByIdFil(idFil).stream().map(this::toDto).collect(Collectors.toList());
    }

    public MasforDTO getById(Long id, Integer idFil) {
        return repository.findByIdAndIdFil(id, idFil).map(this::toDto).orElse(null);
    }

    @Transactional
    public MasforDTO create(MasforDTO dto, String user) {
        Masfor m = fromDto(dto);
        m.setCriadoPor(user);
        Masfor saved = repository.save(m);
        return toDto(saved);
    }

    @Transactional
    public MasforDTO update(Long id, MasforDTO dto, Integer idFil, String user) {
        return repository.findByIdAndIdFil(id, idFil).map(existing -> {
            existing.setCodigo(dto.getCodigo());
            existing.setDescricao(dto.getDescricao());
            existing.setAtivo(dto.getAtivo());
            existing.setAtualizadoPor(user);
            Masfor saved = repository.save(existing);
            return toDto(saved);
        }).orElse(null);
    }

    @Transactional
    public boolean delete(Long id, Integer idFil) {
        return repository.findByIdAndIdFil(id, idFil).map(entity -> { repository.delete(entity); return true; }).orElse(false);
    }

    private MasforDTO toDto(Masfor m) {
        MasforDTO d = new MasforDTO();
        d.setId(m.getId());
        d.setCodigo(m.getCodigo());
        d.setDescricao(m.getDescricao());
        d.setAtivo(m.getAtivo());
        d.setIdFil(m.getIdFil());
        return d;
    }

    private Masfor fromDto(MasforDTO d) {
        Masfor m = new Masfor();
        m.setCodigo(d.getCodigo());
        m.setDescricao(d.getDescricao());
        m.setAtivo(d.getAtivo());
        m.setIdFil(d.getIdFil());
        return m;
    }
}

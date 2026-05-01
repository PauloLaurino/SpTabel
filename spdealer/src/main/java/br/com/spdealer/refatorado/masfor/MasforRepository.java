package br.com.spdealer.refatorado.masfor;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MasforRepository extends JpaRepository<Masfor, Long> {
    List<Masfor> findByIdFil(Integer idFil);
    Optional<Masfor> findByIdAndIdFil(Long id, Integer idFil);
    boolean existsByCodigoAndIdFil(String codigo, Integer idFil);
}

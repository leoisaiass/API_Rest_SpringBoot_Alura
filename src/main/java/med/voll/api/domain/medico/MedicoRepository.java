package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Pegando apenas os médicos que estejam ativos de forma paginada
    Page<Medico> findAllByAtivoTrue(Pageable paginacao);
}
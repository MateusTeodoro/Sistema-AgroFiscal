package agroFiscal.repository;

import agroFiscal.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    
    List<NotaFiscal> findByStatus(String status);
    
}

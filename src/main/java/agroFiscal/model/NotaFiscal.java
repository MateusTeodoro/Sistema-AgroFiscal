package agroFiscal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_nota_fiscal")
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String numero;

    @Column(nullable = false)
    private LocalDate dataEmissao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false, length = 30)
    private String status; // Ex: PENDENTE, AGUARDANDO_ENTREGA, PAGA

    // Relacionamento N:1 (Várias notas pertencem a um único fornecedor)
    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;
    
    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL)
    private List<ItemNota> itens;

}

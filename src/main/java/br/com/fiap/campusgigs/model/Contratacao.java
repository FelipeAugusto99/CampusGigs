package br.com.fiap.campusgigs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contratacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contratacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contratante_id", nullable = false)
    private Usuario contratante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoContratacao situacao;
}
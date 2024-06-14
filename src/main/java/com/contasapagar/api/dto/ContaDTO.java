package com.contasapagar.api.dto;

import com.contasapagar.domain.conta.SituacaoConta;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContaDTO {

  private Long id;

  @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
  private LocalDate dataVencimento;

  @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
  private LocalDate dataPagamento;

  private BigDecimal valor;

  private String descricao;

  private SituacaoConta situacao;
}

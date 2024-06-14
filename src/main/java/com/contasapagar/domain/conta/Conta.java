package com.contasapagar.domain.conta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Conta {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "data_vencimento")
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private LocalDate dataVencimento;

  @Column(name = "data_pagamento")
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private LocalDate dataPagamento;

  private BigDecimal valor;

  private String descricao;

  @Enumerated(EnumType.STRING)
  private SituacaoConta situacao;
}

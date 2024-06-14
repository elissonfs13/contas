package com.contasapagar.domain.conta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

  Page<Conta> findByDescricao(String descricao, Pageable pageable);

  Page<Conta> findByDataVencimento(LocalDate dataVencimento, Pageable pageable);

  Page<Conta> findByDataVencimentoAndDescricao(LocalDate dataVencimento, String descricao, Pageable pageable);

  @Query(value = "SELECT SUM(c.valor) FROM Conta c " +
      "WHERE c.data_pagamento BETWEEN DATE(:startDate) AND DATE(:endDate)",
      nativeQuery = true)
  BigDecimal getValorTotalPorPeriodo(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);
}

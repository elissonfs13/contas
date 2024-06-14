package com.contasapagar.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltroContaDTO {

  @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
  private LocalDate dataVencimento;

  private String descricao;
}

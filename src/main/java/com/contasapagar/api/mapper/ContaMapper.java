package com.contasapagar.api.mapper;

import com.contasapagar.domain.conta.Conta;
import com.contasapagar.api.dto.ContaDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContaMapper {

  private final ModelMapper modelMapper = new ModelMapper();

  public ContaDTO map(Conta conta) {
    return modelMapper.map(conta, ContaDTO.class);
  }

  public Conta map(ContaDTO conta) {
    return modelMapper.map(conta, Conta.class);
  }

  public List<ContaDTO> map(List<Conta> contas) {
    return contas.stream().map(this::map).collect(Collectors.toList());
  }
}

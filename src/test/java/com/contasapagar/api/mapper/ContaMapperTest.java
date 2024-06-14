package com.contasapagar.api.mapper;

import com.contasapagar.api.dto.ContaDTO;
import com.contasapagar.domain.conta.Conta;
import com.contasapagar.domain.conta.SituacaoConta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ContaMapperTest {

  private static final Long CONTA_ID = 1L;

  @InjectMocks
  private ContaMapper contaMapper;

  @Test
  public void testMapContaToContaDTO() {
    Conta conta = criaConta();
    ContaDTO contaDTO = contaMapper.map(conta);
    assertEquals(contaDTO.getValor(), conta.getValor());
    assertEquals(contaDTO.getDataVencimento(), conta.getDataVencimento());
    assertEquals(contaDTO.getDataPagamento(), conta.getDataPagamento());
    assertEquals(contaDTO.getDescricao(), conta.getDescricao());
    assertEquals(contaDTO.getSituacao(), conta.getSituacao());
    assertEquals(contaDTO.getId(), conta.getId());
  }

  @Test
  public void testMapContaDTOToConta() {
    ContaDTO contaDTO = criaContaDTO();
    Conta conta = contaMapper.map(contaDTO);
    assertEquals(conta.getValor(), contaDTO.getValor());
    assertEquals(conta.getDataVencimento(), contaDTO.getDataVencimento());
    assertEquals(conta.getDataPagamento(), contaDTO.getDataPagamento());
    assertEquals(conta.getDescricao(), contaDTO.getDescricao());
    assertEquals(conta.getSituacao(), contaDTO.getSituacao());
    assertEquals(conta.getId(), contaDTO.getId());
  }

  @Test
  public void testMapListaContaToListaContaDTO() {
    List<Conta> contas = new ArrayList<>();
    contas.addAll(criaListaConta());
    List<ContaDTO> contasDTO = contaMapper.map(contas);
    assertEquals(contasDTO.size(), contas.size());
  }

  private List<Conta> criaListaConta() {
    List<Conta> contas = new ArrayList<>();
    contas.add(criaConta());
    contas.add(criaConta());
    contas.add(criaConta());
    contas.add(criaConta());
    contas.add(criaConta());
    return contas;
  }

  private Conta criaConta() {
    return Conta.builder()
        .dataPagamento(LocalDate.now())
        .dataVencimento(LocalDate.now())
        .descricao("Teste Mock Conta Paga")
        .situacao(SituacaoConta.PAGA)
        .valor(new BigDecimal(10))
        .id(CONTA_ID)
        .build();
  }

  private ContaDTO criaContaDTO() {
    return ContaDTO.builder()
        .dataPagamento(LocalDate.now())
        .dataVencimento(LocalDate.now())
        .descricao("Teste Mock Conta Paga")
        .situacao(SituacaoConta.PAGA)
        .valor(new BigDecimal(10))
        .id(CONTA_ID)
        .build();
  }

}

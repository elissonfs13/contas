package com.contasapagar.domain.conta;

import com.contasapagar.exception.ContasAPagarException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContaServiceTest {

  private static final Long CONTA_ID = 1L;
  private static final String CONTA_ALTERADA = "Conta alterada";
  private Conta contaPendente, contaPaga, contaAlterada;

  @InjectMocks
  private ContaService contaService;

  @Mock
  private ContaRepository repository;

  @Mock
  private Pageable pageableMock;

  @Before
  public void init() {
    contaPendente = criaContaPendenteMocada();
    contaPaga = criaContaPagaMocada();
    contaAlterada = criaContaPendenteMocada();
    contaAlterada.setDescricao(CONTA_ALTERADA);
  }

  @Test
  public void findByIdTest(){
    when(repository.findById(CONTA_ID)).thenReturn(Optional.of(contaPendente));
    Optional<Conta> contaRetornada = contaService.findById(CONTA_ID.toString());
    assertNotNull(contaRetornada);
    assertTrue(contaRetornada.isPresent());
    assertEquals(contaRetornada.get(), contaPendente);
    verify(repository, times(1)).findById(CONTA_ID);
  }

  @Test
  public void findByIdComIdNaoEncontradoTest(){
    Optional<Conta> contaRetornada = contaService.findById("2");
    assertNotNull(contaRetornada);
    assertFalse(contaRetornada.isPresent());
    assertTrue(contaRetornada.isEmpty());
    verify(repository, times(0)).findById(CONTA_ID);
  }

  @Test
  public void findByIdComNumeroInvalidoTest(){
    Optional<Conta> contaRetornada = contaService.findById("qualquer coisa diferente de número");
    assertNotNull(contaRetornada);
    assertFalse(contaRetornada.isPresent());
    assertTrue(contaRetornada.isEmpty());
    verify(repository, times(0)).findById(CONTA_ID);
  }

  @Test
  public void criarContaTest() {
    when(repository.save(any())).thenReturn(contaPendente);
    assertNotNull(contaService.criarConta(contaPendente));
    verify(repository, times(1)).save(contaPendente);
  }

  @Test
  public void obterValorTotalPagoTest() {
    when(repository.getValorTotalPorPeriodo(any(), any())).thenReturn(contaPaga.getValor());
    BigDecimal totalPago = contaService.obterValorTotalPago(any(), any());
    assertNotNull(totalPago);
    assertEquals(totalPago, contaPaga.getValor());
    verify(repository, times(1)).getValorTotalPorPeriodo(any(), any());
  }

  @Test
  public void atualizaContaTest() {
    when(repository.findById(CONTA_ID)).thenReturn(Optional.of(contaPendente));
    when(repository.save(any())).thenReturn(contaAlterada);
    Optional<Conta> contaRetornada = contaService.atualizaConta(CONTA_ID.toString(), contaAlterada);
    assertNotNull(contaRetornada);
    assertEquals(contaRetornada.get().getDescricao(), CONTA_ALTERADA);
    verify(repository, times(1)).save(contaPendente);
  }

  @Test
  public void atualizaContaComIdNaoEncontradoTest() {
    Optional<Conta> contaRetornada = contaService.atualizaConta("2", contaAlterada);
    assertFalse(contaRetornada.isPresent());
    assertTrue(contaRetornada.isEmpty());
    verify(repository, times(0)).save(contaPendente);
  }

  @Test
  public void atualizaSituacaoContaTest() {
    when(repository.findById(CONTA_ID)).thenReturn(Optional.of(contaPendente));
    when(repository.save(any())).thenReturn(contaPaga);
    Optional<Conta> contaRetornada = contaService.atualizaSituacaoConta(CONTA_ID.toString());
    assertNotNull(contaRetornada);
    assertEquals(contaRetornada.get().getSituacao(), SituacaoConta.PAGA);
    verify(repository, times(1)).save(contaPendente);
  }

  @Test
  public void atualizaSituacaoContaComIdNaoEncontradoTest() {
    Optional<Conta> contaRetornada = contaService.atualizaSituacaoConta("2");
    assertFalse(contaRetornada.isPresent());
    assertTrue(contaRetornada.isEmpty());
    verify(repository, times(0)).save(contaPendente);
  }

  @Test
  public void listaPorFiltroDataVencimentoEDescricaoTest() {
    contaService.listaPorFiltro(pageableMock, LocalDate.now(), CONTA_ALTERADA);
    verify(repository, times(1)).findByDataVencimentoAndDescricao(any(), any(), eq(pageableMock));
    verify(repository, times(0)).findByDataVencimento(any(), eq(pageableMock));
    verify(repository, times(0)).findByDescricao(any(), eq(pageableMock));
    verify(repository, times(0)).findAll(eq(pageableMock));
  }

  @Test
  public void listaPorFiltroDataVencimentoTest() {
    contaService.listaPorFiltro(pageableMock, LocalDate.now(), null);
    verify(repository, times(0)).findByDataVencimentoAndDescricao(any(), any(), eq(pageableMock));
    verify(repository, times(1)).findByDataVencimento(any(), eq(pageableMock));
    verify(repository, times(0)).findByDescricao(any(), eq(pageableMock));
    verify(repository, times(0)).findAll(eq(pageableMock));
  }

  @Test
  public void listaPorFiltroDescricaoTest() {
    contaService.listaPorFiltro(pageableMock, null, CONTA_ALTERADA);
    verify(repository, times(0)).findByDataVencimentoAndDescricao(any(), any(), eq(pageableMock));
    verify(repository, times(0)).findByDataVencimento(any(), eq(pageableMock));
    verify(repository, times(1)).findByDescricao(any(), eq(pageableMock));
    verify(repository, times(0)).findAll(eq(pageableMock));
  }

  @Test
  public void listaPorFiltroTest() {
    contaService.listaPorFiltro(pageableMock, null, null);
    verify(repository, times(0)).findByDataVencimentoAndDescricao(any(), any(), eq(pageableMock));
    verify(repository, times(0)).findByDataVencimento(any(), eq(pageableMock));
    verify(repository, times(0)).findByDescricao(any(), eq(pageableMock));
    verify(repository, times(1)).findAll(eq(pageableMock));
  }

  @Test
  public void importarCsvTest() throws IOException {
    MultipartFile multipartFile = new MockMultipartFile("teste.csv","teste.csv","text/csv",
        new FileInputStream("src/test/resources/teste.csv"));

    contaService.importarCsv(multipartFile);
    verify(repository, times(1)).saveAll(anyCollection());
  }

  @Test(expected = ContasAPagarException.class)
  public void importarArquivoOutroFormatoTest() throws IOException {
    MultipartFile multipartFile = new MockMultipartFile("teste.pdf","teste.pdf","text/pdf",
        new FileInputStream("src/test/resources/teste.pdf"));

    contaService.importarCsv(multipartFile);
  }

  private Conta criaContaPendenteMocada() {
    return Conta.builder()
        .dataVencimento(LocalDate.now())
        .descricao("Teste Mock Conta Pendente")
        .situacao(SituacaoConta.PENDENTE)
        .valor(new BigDecimal(10))
        .id(CONTA_ID)
        .build();
  }

  private Conta criaContaPagaMocada() {
    return Conta.builder()
        .dataPagamento(LocalDate.now())
        .dataVencimento(LocalDate.now())
        .descricao("Teste Mock Conta Paga")
        .situacao(SituacaoConta.PAGA)
        .valor(new BigDecimal(10))
        .id(CONTA_ID)
        .build();
  }

}

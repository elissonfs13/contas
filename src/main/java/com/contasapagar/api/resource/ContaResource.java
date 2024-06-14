package com.contasapagar.api.resource;

import com.contasapagar.api.dto.ContaDTO;
import com.contasapagar.api.dto.FiltroContaDTO;
import com.contasapagar.api.dto.PeriodoDTO;
import com.contasapagar.api.mapper.ContaMapper;
import com.contasapagar.domain.conta.Conta;
import com.contasapagar.domain.conta.ContaService;
import com.contasapagar.exception.ContasAPagarException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/contas")
public class ContaResource {

  private final ContaService service;

  private final ContaMapper mapper;

  public ContaResource(ContaService service, ContaMapper mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PostMapping
  public ResponseEntity<ContaDTO> criarConta(@RequestBody ContaDTO novaConta) {
    Conta contaCadastrada = service.criarConta(mapper.map(novaConta));
    log.info("Conta cadastrada com sucesso - id: {}", contaCadastrada.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(contaCadastrada));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ContaDTO> atualizaConta(@PathVariable String id, @RequestBody ContaDTO contaAlterada) {
    log.info("Atualização de conta com id: {}", id);
    Optional<Conta> optConta = service.atualizaConta(id, mapper.map(contaAlterada));
    return optConta.isPresent() ?
        ResponseEntity.status(HttpStatus.OK).body(mapper.map(optConta.get())) :
        ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @PutMapping("/situacao/{id}")
  public ResponseEntity<ContaDTO> atualizaSituacaoConta(@PathVariable String id) {
    log.info("Atualização da situação de conta com id: {}", id);
    Optional<Conta> optConta = service.atualizaSituacaoConta(id);
    return optConta.isPresent() ?
        ResponseEntity.status(HttpStatus.OK).body(mapper.map(optConta.get())) :
        ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @GetMapping("/filtro")
  public ResponseEntity<Page<ContaDTO>> obterContas(Pageable pageable, @RequestBody FiltroContaDTO filtro) {
    Page<Conta> contas = service.listaPorFiltro(pageable, filtro.getDataVencimento(), filtro.getDescricao());
    List<ContaDTO> content = mapper.map(contas.getContent());
    return ResponseEntity.status(HttpStatus.OK).body(new PageImpl<>(content));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContaDTO> obterConta(@PathVariable String id) {
    log.info("Consulta de conta com id: {}", id);
    Optional<Conta> optConta = service.findById(id);
    return optConta.isPresent() ?
        ResponseEntity.status(HttpStatus.OK).body(mapper.map(optConta.get())) :
        ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @GetMapping("/valortotal")
  public ResponseEntity<BigDecimal> obterValorTotalPago(@RequestBody PeriodoDTO periodo) {
    log.info("Consulta do valor total pago no período entre {} e {}", periodo.getDataInicio(), periodo.getDataFim());
    BigDecimal valorTotal = service.obterValorTotalPago(periodo.getDataInicio(), periodo.getDataFim());
    return ResponseEntity.status(HttpStatus.OK).body(valorTotal);
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      service.importarCsv(file);
      return ResponseEntity.status(HttpStatus.OK).body("Arquivo CSV importado com sucesso!");
    } catch (ContasAPagarException exception) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao importar arquivo CSV: " + exception);
    }
  }
}

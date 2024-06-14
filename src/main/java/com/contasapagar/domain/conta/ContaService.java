package com.contasapagar.domain.conta;

import com.contasapagar.exception.ContasAPagarException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ContaService {

  public static String DATE_FORMAT = "d/M/yyyy";

  public static String TYPE = "text/csv";

  private final ContaRepository repository;

  public ContaService(ContaRepository repository) {
    this.repository = repository;
  }

  public Optional<Conta> findById(String id) {
    try {
      return repository.findById(Long.parseLong(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public Conta criarConta(Conta conta) {
    return repository.save(conta);
  }

  public BigDecimal obterValorTotalPago(LocalDate dataInicio, LocalDate dataFim) {
    return repository.getValorTotalPorPeriodo(dataInicio, dataFim);
  }

  public Optional<Conta> atualizaConta(String id, Conta contaAlterada) {
    Optional<Conta> optConta = findById(id);
    if (optConta.isPresent()) {
      Conta conta = optConta.get();
      conta.setDataPagamento(contaAlterada.getDataPagamento());
      conta.setDataVencimento(contaAlterada.getDataVencimento());
      conta.setDescricao(contaAlterada.getDescricao());
      conta.setSituacao(contaAlterada.getSituacao());
      conta.setValor(contaAlterada.getValor());
      return Optional.of(repository.save(conta));
    }
    return Optional.empty();
  }

  public Optional<Conta> atualizaSituacaoConta(String id) {
    Optional<Conta> optConta = findById(id);
    if (optConta.isPresent()) {
      Conta conta = optConta.get();
      conta.setSituacao(SituacaoConta.PAGA);
      conta.setDataPagamento(LocalDate.now());
      return Optional.of(repository.save(conta));
    }
    return Optional.empty();
  }

  public Page<Conta> listaPorFiltro(Pageable pageable, LocalDate dataVencimento, String descricao) {
    if (dataVencimento != null) {
      if (descricao != null) {
        return repository.findByDataVencimentoAndDescricao(dataVencimento, descricao, pageable);
      }
      return repository.findByDataVencimento(dataVencimento, pageable);
    }
    if (descricao != null) {
      return repository.findByDescricao(descricao, pageable);
    }
    return repository.findAll(pageable);
  }

  public void importarCsv(MultipartFile file) {
    try {
      if (!TYPE.equals(file.getContentType())) {
        log.error("Tipo de arquivo inválido");
        throw new ContasAPagarException("Tipo de arquivo inválido");
      }
      List<Conta> contas = csvParaContas(file.getInputStream());
      repository.saveAll(contas);
    } catch (IOException e) {
      log.error("Erro ao converter arquivo CSV: {}", e.getMessage());
      throw new ContasAPagarException("Erro ao converter arquivo CSV: " + e.getMessage());
    }
  }

  private List<Conta> csvParaContas(InputStream is) {
    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
         CSVParser csvParser = new CSVParser(fileReader,
             CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

      List<Conta> contas = new ArrayList<>();
      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {
        Conta conta = Conta.builder()
            .dataVencimento(getDataCsv(csvRecord.get("DataVencimento")))
            .dataPagamento(getDataCsv(csvRecord.get("DataPagamento")))
            .valor(new BigDecimal(csvRecord.get("Valor")))
            .descricao(csvRecord.get("Descricao"))
            .situacao(SituacaoConta.valueOf(csvRecord.get("Situacao")))
            .build();

        contas.add(conta);
      }

      return contas;
    } catch (IOException e) {
      log.error("Erro ao converter arquivo CSV: {}", e.getMessage());
      throw new ContasAPagarException("Erro ao converter arquivo CSV: " + e.getMessage());
    }
  }

  private LocalDate getDataCsv(String data) {
    try {
      return data.isBlank() ? null : LocalDate.parse(data, DateTimeFormatter.ofPattern(DATE_FORMAT));
    } catch (DateTimeParseException e) {
      log.error("Erro ao converter data encontrada no arquivo CSV: {}", e.getMessage());
      throw new ContasAPagarException("Erro ao converter data encontrada no arquivo CSV: " + e.getMessage());
    }
  }
}

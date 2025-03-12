package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    // Injeção de dependência de uma classe controlada e reconhecida pelo SpringBoot
    @Autowired
    MedicoRepository repository;

    // Requisição POST do protocolo HTTP - Insere alguma coisa no banco de dados
    @PostMapping
    // Indicando que vai ter uma transação com o banco de dados
    @Transactional
    // ResponseEntity para ser possível manipular os tipos de retorno HTTP
    public ResponseEntity cadastrar(
            // Pegando o corpo da requisição (JSON) e validando os campos recebidos
            @RequestBody @Valid DadosCadastroMedico dados,
            // Classe para criar uma URI
            UriComponentsBuilder uriBuilder) {

        var medico = new Medico(dados);
        // Salvando os registros no banco de dados
        repository.save(medico);
        // Criando uma uri, com um novo caminho, passando o novo id criado
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        // Retornando a URI criada contendo em seu corpo os detalhamentos do novo médico salvo
        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    // Requisição GET do HTTP - Retorna alguma coisa para o cliente
    @GetMapping
    // Page para indicar que contém paginação e que está retorna listagem de médicos
    public ResponseEntity<Page<DadosListagemMedico>> listar(
            // Configurações da paginação
            @PageableDefault(
                    // Tamanho e ordenação
                    size = 10, sort = {"nome"})
            // Indicando que deve ser pageável
            Pageable paginacao) {
        // Busca todos os médicos ativos com a configuração de paginação
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
        // Retorna a resposta com status 200 (OK) e a página de médicos
        return ResponseEntity.ok(page);
    }

    // Requisição PUT do HTTP - Atualiza algum registro do banco de dados
    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        // Pegando a referência do médico por ID
        var medico = repository.getReferenceById(dados.id());
        // Atualizando as informações do médico através de um metodo da própria classe
        medico.atualizarInformacoes(dados);
        // Retornando os detalhes do médico com as novas alterações
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    // Requisição DELETE do HTTP - Deleta algum registro do banco de dados
    // Dentro do parâmetro é expandido a URL contendo o id como variável
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(
            // Indicando que esta variável é que foi passada no caminho da URL
            @PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();
        // Retornando que foi feito com sucesso, mas não há conteúdo para retorno
        return ResponseEntity.noContent().build();
    }

    // Nova requisição GET com variável de id
    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        // Retornando os detalhamentos do médico
        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }
}
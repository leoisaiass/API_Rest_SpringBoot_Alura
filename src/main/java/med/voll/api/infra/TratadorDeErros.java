package med.voll.api.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Anotação indicando que é um classe global de configuração de erros/exceções
@RestControllerAdvice
public class TratadorDeErros {

    // Anotação para indicar que é um metodo para lidar com erro/exceção
    // Neste caso quando não for encontrada entidade na requisição
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    // Quando os argumentos não passarem pelo validador
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        // Pegando a lista de erros
        var erros = ex.getFieldErrors();
        // Retornando apenas os erros que estão mapeados no DTO
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    // DTO para pegar apenas campo e a mensagem do retorno do erro
    private record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}

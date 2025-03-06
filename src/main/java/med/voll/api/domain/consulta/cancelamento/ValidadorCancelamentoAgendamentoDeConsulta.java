package med.voll.api.domain.consulta.cancelamento;

import med.voll.api.domain.consulta.DadosCancelamentoConsulta;

public interface ValidadorCancelamentoAgendamentoDeConsulta {

    void validar(DadosCancelamentoConsulta dados);

}

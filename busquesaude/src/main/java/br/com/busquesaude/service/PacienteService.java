package br.com.busquesaude.service;

import br.com.busquesaude.model.Paciente;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PacienteService {

    // Armazenamento em memória: a camada Repository/JPA entra na Semana 05.
    private final Map<Long, Paciente> pacientes = new LinkedHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public synchronized Paciente cadastrar(Paciente paciente) {
        validarCpfDuplicado(paciente.getCpf(), null); // regra de negócio (não é validação estrutural)
        paciente.setId(sequencia.getAndIncrement());
        pacientes.put(paciente.getId(), paciente);
        return paciente;
    }

    public synchronized Paciente atualizar(Long id, Paciente novo) {
        Paciente existente = pacientes.get(id);
        if (existente == null) return null;
        validarCpfDuplicado(novo.getCpf(), id);
        existente.setNome(novo.getNome());
        existente.setCpf(novo.getCpf());
        existente.setDataNascimento(novo.getDataNascimento());
        existente.setEmail(novo.getEmail());
        existente.setTelefone(novo.getTelefone());
        existente.setConvenio(novo.getConvenio());
        return existente;
    }

    public synchronized Paciente consultar(Long id) { return pacientes.get(id); }

    public synchronized boolean excluir(Long id) { return pacientes.remove(id) != null; }

    public synchronized List<Paciente> listar() { return new ArrayList<>(pacientes.values()); }

    private void validarCpfDuplicado(String cpf, Long idIgnorado) {
        boolean duplicado = pacientes.values().stream()
                .anyMatch(p -> p.getCpf().equals(cpf) && !p.getId().equals(idIgnorado));
        if (duplicado) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe paciente com este CPF.");
    }
}

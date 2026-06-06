package com.smart.reto.cliente;

import com.smart.reto.cliente.dto.ClienteRequest;
import com.smart.reto.cliente.dto.ClienteResponse;
import com.smart.reto.shared.RecursoNoEncontradoException;
import com.smart.reto.shared.ReglaNegocioException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (repository.existsByCorreo(request.correo())) {
            throw new ReglaNegocioException("Ya existe un cliente con el correo " + request.correo());
        }
        Cliente c = new Cliente();
        c.setNombre(request.nombre());
        c.setCorreo(request.correo());
        c.setTelefono(request.telefono());
        return aResponse(repository.save(c));
    }

    @Transactional
    public ClienteResponse editar(Long id, ClienteRequest request) {
        Cliente c = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente " + id));
        c.setNombre(request.nombre());
        c.setCorreo(request.correo());
        c.setTelefono(request.telefono());
        return aResponse(c);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe el cliente " + id);
        }
        repository.deleteById(id);
    }

    private ClienteResponse aResponse(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNombre(), c.getCorreo(), c.getTelefono());
    }
}

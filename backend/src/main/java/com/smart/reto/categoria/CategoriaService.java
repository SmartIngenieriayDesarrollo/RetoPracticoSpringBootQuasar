package com.smart.reto.categoria;

import com.smart.reto.categoria.dto.CategoriaRequest;
import com.smart.reto.categoria.dto.CategoriaResponse;
import com.smart.reto.shared.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria c = new Categoria();
        c.setNombre(request.nombre());
        c.setDescripcion(request.descripcion());
        return aResponse(repository.save(c));
    }

    @Transactional
    public CategoriaResponse editar(Long id, CategoriaRequest request) {
        Categoria c = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoria " + id));
        c.setNombre(request.nombre());
        c.setDescripcion(request.descripcion());
        return aResponse(c);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe la categoria " + id);
        }
        repository.deleteById(id);
    }

    private CategoriaResponse aResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }
}

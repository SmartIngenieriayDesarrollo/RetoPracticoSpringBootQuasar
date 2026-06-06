package com.smart.reto.empresa;

import com.smart.reto.empresa.dto.EmpresaRequest;
import com.smart.reto.empresa.dto.EmpresaResponse;
import com.smart.reto.shared.RecursoNoEncontradoException;
import com.smart.reto.shared.ReglaNegocioException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper mapper;

    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper mapper) {
        this.empresaRepository = empresaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> listar() {
        return empresaRepository.findAll().stream()
                .map(mapper::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obtener(String nit) {
        Empresa empresa = buscarOExcepcion(nit);
        return mapper.aResponse(empresa);
    }

    @Transactional
    public EmpresaResponse crear(EmpresaRequest request) {
        if (empresaRepository.existsById(request.nit())) {
            throw new ReglaNegocioException("Ya existe una empresa con el NIT " + request.nit());
        }
        Empresa creada = empresaRepository.save(mapper.aEntidad(request));
        return mapper.aResponse(creada);
    }

    @Transactional
    public EmpresaResponse editar(String nit, EmpresaRequest request) {
        Empresa empresa = buscarOExcepcion(nit);
        mapper.copiarEditables(request, empresa);

        return mapper.aResponse(empresa);
    }

    @Transactional
    public void eliminar(String nit) {
        Empresa empresa = buscarOExcepcion(nit);

        empresaRepository.delete(empresa);
    }

    private Empresa buscarOExcepcion(String nit) {
        return empresaRepository.findById(nit)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una empresa con el NIT " + nit));
    }
}

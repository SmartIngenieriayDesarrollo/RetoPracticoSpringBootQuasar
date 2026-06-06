package com.smart.reto.empresa;

import com.smart.reto.empresa.dto.EmpresaRequest;
import com.smart.reto.empresa.dto.EmpresaResponse;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public EmpresaResponse aResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getNit(),
                empresa.getNombre(),
                empresa.getDireccion(),
                empresa.getTelefono()
        );
    }

    public Empresa aEntidad(EmpresaRequest request) {
        Empresa empresa = new Empresa();
        empresa.setNit(request.nit());
        empresa.setNombre(request.nombre());
        empresa.setDireccion(request.direccion());
        empresa.setTelefono(request.telefono());
        return empresa;
    }

    public void copiarEditables(EmpresaRequest request, Empresa destino) {
        destino.setNombre(request.nombre());
        destino.setDireccion(request.direccion());
        destino.setTelefono(request.telefono());
    }
}

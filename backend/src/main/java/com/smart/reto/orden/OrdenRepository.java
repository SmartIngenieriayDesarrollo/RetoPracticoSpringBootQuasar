package com.smart.reto.orden;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    @EntityGraph(attributePaths = {"items", "items.producto", "cliente"})
    List<Orden> findByClienteId(Long clienteId);
}

package com.smart.reto.producto;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @EntityGraph(attributePaths = {"precios", "categorias", "empresa"})
    List<Producto> findByEmpresaNit(String nit);

    @EntityGraph(attributePaths = {"precios", "categorias", "empresa"})
    Optional<Producto> findWithDetailById(Long id);

    boolean existsByCodigo(String codigo);
}

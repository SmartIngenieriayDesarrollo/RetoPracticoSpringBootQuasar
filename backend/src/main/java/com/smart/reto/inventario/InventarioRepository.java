package com.smart.reto.inventario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    @EntityGraph(attributePaths = {"producto", "producto.empresa"})
    @Query("SELECT i FROM Inventario i")
    List<Inventario> findAllConProducto();

    @EntityGraph(attributePaths = {"producto", "producto.empresa"})
    @Query("SELECT i FROM Inventario i WHERE i.producto.empresa.nit = :nit")
    List<Inventario> findByEmpresaNit(@Param("nit") String nit);

    Optional<Inventario> findByProductoId(Long productoId);
}

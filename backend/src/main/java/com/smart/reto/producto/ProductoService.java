package com.smart.reto.producto;

import com.smart.reto.categoria.Categoria;
import com.smart.reto.categoria.CategoriaRepository;
import com.smart.reto.empresa.Empresa;
import com.smart.reto.empresa.EmpresaRepository;
import com.smart.reto.producto.dto.PrecioDto;
import com.smart.reto.producto.dto.ProductoRequest;
import com.smart.reto.producto.dto.ProductoResponse;
import com.smart.reto.shared.RecursoNoEncontradoException;
import com.smart.reto.shared.ReglaNegocioException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper mapper;

    public ProductoService(ProductoRepository productoRepository,
                           EmpresaRepository empresaRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoMapper mapper) {
        this.productoRepository = productoRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorEmpresa(String nit) {
        return productoRepository.findByEmpresaNit(nit).stream()
                .map(mapper::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        Producto p = productoRepository.findWithDetailById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + id));
        return mapper.aResponse(p);
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.codigo())) {
            throw new ReglaNegocioException("Ya existe un producto con el codigo " + request.codigo());
        }
        Empresa empresa = empresaRepository.findById(request.empresaNit())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la empresa " + request.empresaNit()));

        validarMonedasUnicas(request.precios());

        Producto producto = new Producto();
        producto.setCodigo(request.codigo());
        producto.setNombre(request.nombre());
        producto.setCaracteristicas(request.caracteristicas());
        producto.setEmpresa(empresa);
        aplicarPrecios(producto, request.precios());
        producto.setCategorias(resolverCategorias(request.categoriaIds()));

        Producto guardado = productoRepository.save(producto);
        return mapper.aResponse(guardado);
    }

    @Transactional
    public ProductoResponse editar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findWithDetailById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + id));

        validarMonedasUnicas(request.precios());

        producto.setNombre(request.nombre());
        producto.setCaracteristicas(request.caracteristicas());

        // si cambia la empresa la vuelvo a cargar
        if (!producto.getEmpresa().getNit().equals(request.empresaNit())) {
            Empresa empresa = empresaRepository.findById(request.empresaNit())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe la empresa " + request.empresaNit()));
            producto.setEmpresa(empresa);
        }

        producto.limpiarPrecios();
        aplicarPrecios(producto, request.precios());

        producto.setCategorias(resolverCategorias(request.categoriaIds()));

        return mapper.aResponse(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe el producto " + id);
        }
        productoRepository.deleteById(id);
    }

    private void aplicarPrecios(Producto producto, List<PrecioDto> precios) {
        for (PrecioDto dto : precios) {
            ProductoPrecio precio = new ProductoPrecio();
            precio.setMoneda(dto.moneda().toUpperCase());
            precio.setValor(dto.valor());
            producto.agregarPrecio(precio);
        }
    }

    private void validarMonedasUnicas(List<PrecioDto> precios) {
        // no repetir la misma moneda en un producto
        Set<String> monedas = new HashSet<>();
        for (PrecioDto p : precios) {
            if (!monedas.add(p.moneda().toUpperCase())) {
                throw new ReglaNegocioException("Moneda duplicada en los precios: " + p.moneda());
            }
        }
    }

    private Set<Categoria> resolverCategorias(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Categoria> encontradas = categoriaRepository.findByIdIn(ids);
        if (encontradas.size() != ids.size()) {
            throw new ReglaNegocioException("Una o mas categorias no existen");
        }
        return encontradas;
    }
}

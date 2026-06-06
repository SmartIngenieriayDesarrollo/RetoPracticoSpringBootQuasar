package com.smart.reto.orden;

import com.smart.reto.cliente.Cliente;
import com.smart.reto.cliente.ClienteRepository;
import com.smart.reto.orden.dto.OrdenRequest;
import com.smart.reto.orden.dto.OrdenResponse;
import com.smart.reto.producto.Producto;
import com.smart.reto.producto.ProductoPrecio;
import com.smart.reto.producto.ProductoRepository;
import com.smart.reto.shared.RecursoNoEncontradoException;
import com.smart.reto.shared.ReglaNegocioException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public OrdenService(OrdenRepository ordenRepository,
                        ClienteRepository clienteRepository,
                        ProductoRepository productoRepository) {
        this.ordenRepository = ordenRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarPorCliente(Long clienteId) {
        return ordenRepository.findByClienteId(clienteId).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el cliente " + request.clienteId()));

        String moneda = request.moneda().toUpperCase();

        Orden orden = new Orden();
        orden.setCliente(cliente);
        orden.setEstado("CREADA");

        for (var itemReq : request.items()) {
            Producto producto = productoRepository.findWithDetailById(itemReq.productoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe el producto " + itemReq.productoId()));

            BigDecimal precio = buscarPrecio(producto, moneda);

            OrdenItem item = new OrdenItem();
            item.setProducto(producto);
            item.setCantidad(itemReq.cantidad());
            // guardo el precio del momento como snapshot
            item.setPrecioUnitario(precio);
            item.setMoneda(moneda);
            orden.agregarItem(item);
        }

        Orden guardada = ordenRepository.save(orden);
        return aResponse(guardada);
    }

    private BigDecimal buscarPrecio(Producto producto, String moneda) {
        return producto.getPrecios().stream()
                .filter(p -> p.getMoneda().equalsIgnoreCase(moneda))
                .map(ProductoPrecio::getValor)
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "El producto " + producto.getCodigo()
                                + " no tiene precio en la moneda " + moneda));
    }

    private OrdenResponse aResponse(Orden orden) {
        List<OrdenResponse.OrdenItemResponse> items = orden.getItems().stream()
                .map(i -> {
                    BigDecimal subtotal = i.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(i.getCantidad()));
                    return new OrdenResponse.OrdenItemResponse(
                            i.getProducto().getId(),
                            i.getProducto().getNombre(),
                            i.getCantidad(),
                            i.getPrecioUnitario(),
                            i.getMoneda(),
                            subtotal);
                })
                .toList();

        BigDecimal total = items.stream()
                .map(OrdenResponse.OrdenItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrdenResponse(
                orden.getId(),
                orden.getCliente().getId(),
                orden.getCliente().getNombre(),
                orden.getFecha(),
                orden.getEstado(),
                items,
                total);
    }
}

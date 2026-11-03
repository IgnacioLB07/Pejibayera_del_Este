package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Carrito;
import com.pejibayeraEste.domain.DetalleCarrito;
import com.pejibayeraEste.domain.DetallePedido;
import com.pejibayeraEste.domain.Empresa;
import com.pejibayeraEste.domain.Pedido;
import com.pejibayeraEste.domain.Producto;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.repository.CarritoRepository;
import com.pejibayeraEste.repository.DetalleCarritoRepository;
import com.pejibayeraEste.repository.DetallePedidoRepository;
import com.pejibayeraEste.repository.PedidoRepository;
import com.pejibayeraEste.repository.ProductoRepository;
import com.pejibayeraEste.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompraService {

    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleCarritoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CompraService(
            CarritoRepository carritoRepository,
            DetalleCarritoRepository detalleCarritoRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.detalleCarritoRepository = detalleCarritoRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // -------------------- Carritos --------------------
    @Transactional(readOnly = true)
    public List<Carrito> listarCarritos() {
        return carritoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Carrito> buscarCarrito(Integer id) {
        return carritoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Carrito> buscarCarritoPorUsuario(Integer idUsuario) {
        return carritoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public Carrito guardarCarrito(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    public void eliminarCarrito(Integer id) {
        carritoRepository.deleteById(id);
    }

    private Carrito obtenerOCrearCarrito(Usuario usuario) {
        return carritoRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    nuevo.setEstado("Activo");
                    return carritoRepository.save(nuevo);
                });
    }

    @Transactional(readOnly = true)
    public List<DetalleCarrito> listarDetallesPorUsuario(Integer idUsuario) {
        return carritoRepository.findByUsuario_IdUsuario(idUsuario)
                .map(carrito -> detalleCarritoRepository.findByCarrito_IdCarrito(
                        carrito.getIdCarrito()))
                .orElseGet(ArrayList::new);
    }

    @Transactional(readOnly = true)
    public List<DetalleCarrito> listarDetallesCarrito(Integer idCarrito) {
        return detalleCarritoRepository.findByCarrito_IdCarrito(idCarrito);
    }

    @Transactional(readOnly = true)
    public Optional<DetalleCarrito> buscarDetalleCarrito(Integer id) {
        return detalleCarritoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<DetalleCarrito> buscarDetallePorProducto(Integer idCarrito, Integer idProducto) {
        return detalleCarritoRepository.findByCarrito_IdCarritoAndProducto_IdProducto(
                idCarrito, idProducto);
    }

    public DetalleCarrito guardarDetalleCarrito(DetalleCarrito detalle) {
        return detalleCarritoRepository.save(detalle);
    }

    public void eliminarDetalleCarrito(Integer id) {
        detalleCarritoRepository.deleteById(id);
    }

    public DetalleCarrito agregarProductoAlCarrito(
            Integer idUsuario, Integer idProducto, Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));
        Producto producto = productoRepository.findByIdProductoAndActivoTrue(idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto no existe o está inactivo."));

        validarEmpresaActiva(producto);
        validarExistencias(producto, cantidad);

        Carrito carrito = obtenerOCrearCarrito(usuario);
        if (!"Activo".equalsIgnoreCase(carrito.getEstado())) {
            carrito.setEstado("Activo");
            carritoRepository.save(carrito);
        }

        DetalleCarrito detalle = detalleCarritoRepository
                .findByCarrito_IdCarritoAndProducto_IdProducto(
                        carrito.getIdCarrito(), idProducto)
                .orElseGet(() -> {
                    DetalleCarrito nuevo = new DetalleCarrito();
                    nuevo.setCarrito(carrito);
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(0);
                    return nuevo;
                });

        int nuevaCantidad = detalle.getCantidad() + cantidad;
        validarExistencias(producto, nuevaCantidad);
        detalle.setCantidad(nuevaCantidad);
        detalle.setPrecio(producto.getPrecio());
        return detalleCarritoRepository.save(detalle);
    }

    public DetalleCarrito actualizarCantidadDetalleCarrito(
            Integer idUsuario, Integer idDetalle, Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        DetalleCarrito detalle = buscarDetalleDelUsuario(idUsuario, idDetalle);
        Producto producto = productoRepository.findByIdProductoAndActivoTrue(
                detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto ya no está disponible."));
        validarEmpresaActiva(producto);
        validarExistencias(producto, cantidad);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(producto.getPrecio());
        return detalleCarritoRepository.save(detalle);
    }

    public void eliminarDetalleCarritoDeUsuario(Integer idUsuario, Integer idDetalle) {
        DetalleCarrito detalle = buscarDetalleDelUsuario(idUsuario, idDetalle);
        detalleCarritoRepository.delete(detalle);
    }

    private DetalleCarrito buscarDetalleDelUsuario(Integer idUsuario, Integer idDetalle) {
        DetalleCarrito detalle = detalleCarritoRepository.findById(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto no existe en el carrito."));
        Integer propietario = detalle.getCarrito().getUsuario().getIdUsuario();
        if (!idUsuario.equals(propietario)) {
            throw new IllegalArgumentException("El producto no pertenece a su carrito.");
        }
        return detalle;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCarrito(Integer idUsuario) {
        return listarDetallesPorUsuario(idUsuario).stream()
                .map(detalle -> detalle.getPrecio().multiply(
                        BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // -------------------- Confirmación de compra --------------------
    /**
     * Confirma el carrito dentro de una sola transacción. Si contiene productos
     * de distintas empresas, genera un pedido por empresa, porque la tabla
     * pedido exige un único id_empresa por pedido.
     */
    public List<Pedido> confirmarCompra(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));
        Optional<Carrito> carritoOpt = carritoRepository.findByUsuario_IdUsuario(idUsuario);
        if (carritoOpt.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        List<DetalleCarrito> detalles = detalleCarritoRepository
                .findByCarrito_IdCarrito(carritoOpt.get().getIdCarrito());
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        Map<Integer, List<DetalleCarrito>> detallesPorEmpresa = new LinkedHashMap<>();
        Map<Integer, Empresa> empresas = new LinkedHashMap<>();

        // Bloquea cada producto mientras se valida y descuenta su inventario.
        for (DetalleCarrito detalle : detalles) {
            Producto producto = productoRepository.buscarActivoParaActualizar(
                    detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new IllegalStateException(
                            "Uno de los productos ya no está disponible."));

            validarEmpresaActiva(producto);
            validarExistencias(producto, detalle.getCantidad());
            if (producto.getPrecio() == null) {
                throw new IllegalStateException("Un producto no tiene precio configurado.");
            }

            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());
            Integer idEmpresa = producto.getEmpresa().getIdEmpresa();
            detallesPorEmpresa.computeIfAbsent(idEmpresa, clave -> new ArrayList<>()).add(detalle);
            empresas.putIfAbsent(idEmpresa, producto.getEmpresa());
        }

        List<Pedido> pedidosCreados = new ArrayList<>();
        for (Map.Entry<Integer, List<DetalleCarrito>> entrada : detallesPorEmpresa.entrySet()) {
            Empresa empresa = empresas.get(entrada.getKey());
            List<DetalleCarrito> detallesEmpresa = entrada.getValue();

            BigDecimal total = detallesEmpresa.stream()
                    .map(d -> d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setEmpresa(empresa);
            pedido.setTotal(total);
            pedido.setEstado("Pendiente");
            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            for (DetalleCarrito detalle : detallesEmpresa) {
                Producto producto = detalle.getProducto();
                BigDecimal subtotal = detalle.getPrecio()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));

                DetallePedido detallePedido = new DetallePedido();
                detallePedido.setPedido(pedidoGuardado);
                detallePedido.setProducto(producto);
                detallePedido.setCantidad(detalle.getCantidad());
                detallePedido.setPrecioUnitario(detalle.getPrecio());
                detallePedido.setSubtotal(subtotal);
                detallePedidoRepository.save(detallePedido);

                producto.setExistencias(producto.getExistencias() - detalle.getCantidad());
                productoRepository.save(producto);
            }
            pedidosCreados.add(pedidoGuardado);
        }

        // Se conserva el registro único del carrito por usuario y se vacían sus líneas.
        detalleCarritoRepository.deleteAll(detalles);
        return pedidosCreados;
    }

    private void validarEmpresaActiva(Producto producto) {
        if (producto.getEmpresa() == null
                || !Boolean.TRUE.equals(producto.getEmpresa().getActivo())) {
            throw new IllegalStateException("La empresa de este producto no está activa.");
        }
    }

    private void validarExistencias(Producto producto, Integer cantidad) {
        if (producto.getExistencias() == null || cantidad == null
                || cantidad < 1 || producto.getExistencias() < cantidad) {
            throw new IllegalArgumentException(
                    "No hay existencias suficientes para el producto " + producto.getNombre() + ".");
        }
    }

    // -------------------- Pedidos --------------------
    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorUsuario(Integer idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorEmpresa(Integer idEmpresa) {
        return pedidoRepository.findByEmpresa_IdEmpresaOrderByFechaDesc(idEmpresa);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstadoOrderByFechaDesc(estado);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPedido(Integer id) {
        return pedidoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPedidoDeUsuario(Integer idPedido, Integer idUsuario) {
        return pedidoRepository.findByIdPedidoAndUsuario_IdUsuario(idPedido, idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPedidoDeEmpresa(Integer idPedido, Integer idEmpresa) {
        return pedidoRepository.findByIdPedidoAndEmpresa_IdEmpresa(idPedido, idEmpresa);
    }

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstadoPedido(Integer idPedido, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El pedido con ID " + idPedido + " no existe."));
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("El estado del pedido es obligatorio.");
        }
        if (nuevoEstado.trim().length() > 30) {
            throw new IllegalArgumentException("El estado no puede superar 30 caracteres.");
        }
        pedido.setEstado(nuevoEstado.trim());
        return pedidoRepository.save(pedido);
    }

    public void cancelarPedidoDeUsuario(Integer idPedido, Integer idUsuario) {

        Pedido pedido = pedidoRepository.buscarPedidoParaActualizar(idPedido)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El pedido no existe."));

        // Verificar que el pedido pertenezca al cliente.
        if (pedido.getUsuario() == null
                || !idUsuario.equals(pedido.getUsuario().getIdUsuario())) {
            throw new IllegalArgumentException(
                    "Este pedido no pertenece a su cuenta.");
        }

        // Solo se pueden cancelar pedidos pendientes.
        if (!"Pendiente".equalsIgnoreCase(pedido.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar pedidos pendientes.");
        }

        List<DetallePedido> detalles = detallePedidoRepository
                .findByPedido_IdPedido(idPedido);

        if (detalles.isEmpty()) {
            throw new IllegalStateException(
                    "El pedido no contiene productos para devolver.");
        }

        // Devolver las cantidades al inventario.
        for (DetallePedido detalle : detalles) {

            Producto producto = productoRepository.buscarParaActualizar(
                    detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró uno de los productos del pedido."));

            Integer cantidad = detalle.getCantidad();

            if (cantidad == null || cantidad < 1) {
                throw new IllegalStateException(
                        "La cantidad de un producto no es válida.");
            }

            Integer existenciasActuales = producto.getExistencias();
            if (existenciasActuales == null || existenciasActuales < 0) {
                throw new IllegalStateException(
                        "Las existencias actuales del producto no son válidas.");
            }

            producto.setExistencias(existenciasActuales + cantidad);

            productoRepository.save(producto);
        }

        // Conservar el pedido, pero cambiar su estado.
        pedido.setEstado("Cancelado");
        pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> listarDetallesPedido(Integer idPedido) {
        return detallePedidoRepository.findByPedido_IdPedido(idPedido);
    }

    @Transactional(readOnly = true)
    public Optional<DetallePedido> buscarDetallePedido(Integer id) {
        return detallePedidoRepository.findById(id);
    }

    public DetallePedido guardarDetallePedido(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    public void eliminarDetallePedido(Integer id) {
        detallePedidoRepository.deleteById(id);
    }
}

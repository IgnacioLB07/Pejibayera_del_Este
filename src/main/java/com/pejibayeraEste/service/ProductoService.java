package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Producto;
import com.pejibayeraEste.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean soloActivos) {
        return soloActivos ? productoRepository.findByActivoTrue() : productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductosPorEmpresa(Integer idEmpresa) {
        return productoRepository.findByEmpresa_IdEmpresaAndActivoTrue(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProductoActivo(Integer idProducto) {
        return productoRepository.findByIdProductoAndActivoTrue(idProducto);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Desactiva el producto para conservar pedidos e historial relacionados.
     */
    public void delete(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El producto con ID " + idProducto + " no existe."));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> getTodosProductosPorEmpresa(Integer idEmpresa) {
        return productoRepository.findByEmpresa_IdEmpresa(idEmpresa);
    }

}

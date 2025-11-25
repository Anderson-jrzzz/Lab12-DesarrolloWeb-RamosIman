package com.tecsup.controller;

import com.tecsup.model.Categoria;
import com.tecsup.model.Producto;
import com.tecsup.repository.CategoriaRepository;
import com.tecsup.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository prodRepo;
    private final CategoriaRepository catRepo; // Repositorio para buscar la Categoria

    // Constructor de inyección de dependencias
    public ProductoController(ProductoRepository prodRepo, CategoriaRepository catRepo) {
        this.prodRepo = prodRepo;
        this.catRepo = catRepo;
    }

    // // Listar todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return prodRepo.findAll();
    }

    // // Buscar un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        return prodRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        // Si el objeto viene con categoría (solo el id), cargamos la entidad completa
        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            Categoria categoria = catRepo.findById(producto.getCategoria().getId()).orElse(null);
            producto.setCategoria(categoria);
        }

        Producto nuevo = prodRepo.save(producto);
        return ResponseEntity.ok(nuevo);
    }

    // // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return prodRepo.findById(id)
                .map(pExistente -> {
                    pExistente.setNombre(producto.getNombre());
                    pExistente.setPrecio(producto.getPrecio());
                    pExistente.setStock(producto.getStock());

                    // Lógica para actualizar la Categoria
                    if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
                        Categoria categoria = catRepo.findById(producto.getCategoria().getId()).orElse(null);
                        pExistente.setCategoria(categoria);
                    }

                    Producto actualizado = prodRepo.save(pExistente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        return prodRepo.findById(id)
                .map(p -> {
                    prodRepo.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
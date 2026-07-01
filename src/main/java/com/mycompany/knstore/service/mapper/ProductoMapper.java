package com.mycompany.knstore.service.mapper;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.service.dto.CategoriaDTO;
import com.mycompany.knstore.service.dto.CategoriaIVADTO;
import com.mycompany.knstore.service.dto.MarcaDTO;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.dto.ProductoImagenDTO;
import com.mycompany.knstore.service.dto.ProductoInventarioDTO;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import com.mycompany.knstore.service.dto.SubcategoriaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Producto} and its DTO {@link ProductoDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProductoImagenMapper.class })
public interface ProductoMapper extends EntityMapper<ProductoDTO, Producto> {
    @Mapping(target = "precio", source = "precio", qualifiedByName = "productoPrecioResumen")
    @Mapping(target = "inventario", source = "inventario", qualifiedByName = "productoInventarioResumen")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaResumen")
    @Mapping(target = "subcategoria", source = "subcategoria", qualifiedByName = "subcategoriaResumen")
    @Mapping(target = "marca", source = "marca", qualifiedByName = "marcaResumen")
    @Mapping(target = "categoriaIva", source = "categoriaIva", qualifiedByName = "categoriaIVAResumen")
    @Mapping(target = "imagenes", source = "imageneses")
    ProductoDTO toDto(Producto s);

    @Named("productoPrecioResumen")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "precioCompra", source = "precioCompra")
    @Mapping(target = "precioVenta", source = "precioVenta")
    @Mapping(target = "precioAdicional", source = "precioAdicional")
    @Mapping(target = "ganancia", source = "ganancia")
    ProductoPrecioDTO toDtoProductoPrecioResumen(ProductoPrecio productoPrecio);

    @Named("productoInventarioResumen")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "stock", source = "stock")
    @Mapping(target = "stockMinimo", source = "stockMinimo")
    @Mapping(target = "ubicacionBodega", source = "ubicacionBodega")
    @Mapping(target = "garantiaMeses", source = "garantiaMeses")
    ProductoInventarioDTO toDtoProductoInventarioResumen(ProductoInventario productoInventario);

    @Named("categoriaResumen")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "activo", source = "activo")
    CategoriaDTO toDtoCategoriaResumen(Categoria categoria);

    @Named("subcategoriaResumen")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "activo", source = "activo")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaResumen")
    SubcategoriaDTO toDtoSubcategoriaResumen(Subcategoria subcategoria);

    @Named("marcaResumen")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "slug", source = "slug")
    MarcaDTO toDtoMarcaResumen(Marca marca);

    @Named("categoriaIVAResumen")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "porcentaje", source = "porcentaje")
    @Mapping(target = "estado", source = "estado")
    CategoriaIVADTO toDtoCategoriaIVAResumen(CategoriaIVA categoriaIVA);
}

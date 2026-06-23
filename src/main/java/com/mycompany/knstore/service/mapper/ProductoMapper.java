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
    @Mapping(target = "precio", source = "precio", qualifiedByName = "productoPrecioId")
    @Mapping(target = "inventario", source = "inventario", qualifiedByName = "productoInventarioId")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaNombre")
    @Mapping(target = "subcategoria", source = "subcategoria", qualifiedByName = "subcategoriaNombre")
    @Mapping(target = "marca", source = "marca", qualifiedByName = "marcaId")
    @Mapping(target = "categoriaIva", source = "categoriaIva", qualifiedByName = "categoriaIVAId")
    @Mapping(target = "imagenes", source = "imageneses")
    ProductoDTO toDto(Producto s);

    @Named("productoPrecioId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductoPrecioDTO toDtoProductoPrecioId(ProductoPrecio productoPrecio);

    @Named("productoInventarioId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductoInventarioDTO toDtoProductoInventarioId(ProductoInventario productoInventario);

    @Named("categoriaNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    CategoriaDTO toDtoCategoriaNombre(Categoria categoria);

    @Named("subcategoriaNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    SubcategoriaDTO toDtoSubcategoriaNombre(Subcategoria subcategoria);

    @Named("marcaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MarcaDTO toDtoMarcaId(Marca marca);

    @Named("categoriaIVAId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoriaIVADTO toDtoCategoriaIVAId(CategoriaIVA categoriaIVA);
}

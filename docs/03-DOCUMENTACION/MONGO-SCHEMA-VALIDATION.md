# Schema Validation de MongoDB - KN Store

Este documento contiene el **schema validation completo** para cada colección de MongoDB utilizada por el backend de KN Store (aplicación JHipster + Spring Data MongoDB).

Los esquemas se derivaron directamente de las entidades de dominio ubicadas en `src/main/java/com/mycompany/knstore/domain/` y sus respectivas validaciones de Bean Validation (`jakarta.validation.constraints.*`).

---

## Índice de colecciones

1. [Consideraciones de mapeo](#consideraciones-de-mapeo)
2. [Cómo aplicar los schemas](#cómo-aplicar-los-schemas)
3. [project_user](#1-project_user)
4. [project_authority](#2-project_authority)
5. [categoria](#3-categoria)
6. [subcategoria](#4-subcategoria)
7. [marca](#5-marca)
8. [categoriaiva](#6-categoriaiva)
9. [producto](#7-producto)
10. [producto_precio](#8-producto_precio)
11. [producto_inventario](#9-producto_inventario)
12. [producto_imagen](#10-producto_imagen)
13. [etiqueta_producto](#11-etiqueta_producto)
14. [cuenta](#12-cuenta)
15. [tipo_documento](#13-tipo_documento)
16. [direccion](#14-direccion)
17. [carrito](#15-carrito)
18. [item_carrito](#16-item_carrito)
19. [pedido](#17-pedido)
20. [item_pedido](#18-item_pedido)
21. [pago](#19-pago)
22. [factura](#20-factura)
23. [envio](#21-envio)

---

## Consideraciones de mapeo

| Tipo Java / anotación                          | Representación en MongoDB                                              | Notas                                                                                              |
| ---------------------------------------------- | ---------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| `String` con `@Id`                             | `_id` (`objectId` o `string`)                                          | Spring Data genera `ObjectId` por defecto.                                                         |
| `String`                                       | `string`                                                               | Se respeta `@Field("nombre_campo")`.                                                               |
| `Boolean`                                      | `bool`                                                                 |                                                                                                    |
| `Integer`                                      | `int`                                                                  |                                                                                                    |
| `BigDecimal`                                   | `decimal`                                                              | Almacenado como `Decimal128`.                                                                      |
| `Instant`                                      | `date`                                                                 | Fecha/hora UTC.                                                                                    |
| `LocalDate`                                    | `date`                                                                 | Solo la parte de fecha.                                                                            |
| `byte[]`                                       | `binData`                                                              | Imágenes, archivos, etc.                                                                           |
| Enum de Java                                   | `string`                                                               | Se almacena el **nombre** de la constante (`name()`), salvo que exista un converter personalizado. |
| `@DBRef`                                       | `object` con `$ref`, `$id`, `$db`                                      | Referencias entre colecciones.                                                                     |
| `@NotNull`                                     | `required`                                                             |                                                                                                    |
| `@Size(max = N)`                               | `maxLength: N`                                                         |                                                                                                    |
| `@Size(min = M, max = N)`                      | `minLength: M`, `maxLength: N`                                         |                                                                                                    |
| `@Min(0)` / `@DecimalMin("0")`                 | `minimum: 0`                                                           |                                                                                                    |
| `@DecimalMax("100")`                           | `maximum: 100`                                                         |                                                                                                    |
| `@Pattern`                                     | `pattern`                                                              | Expresión regular de Java adaptada a PCRE cuando es posible.                                       |
| Campos de auditoría (`AbstractAuditingEntity`) | `created_by`, `created_date`, `last_modified_by`, `last_modified_date` | Aplicables a `project_user`.                                                                       |

> **Nota sobre enumeraciones:** Spring Data MongoDB persiste por defecto el nombre de la constante del enum. Si el proyecto utiliza converters que almacenan `getValue()`, los schemas deberán ajustarse a esos valores literales.

---

## Cómo aplicar los schemas

Puedes aplicar cada schema con `db.createCollection()` (si la colección no existe) o `db.runCommand({ collMod: ... })` (si ya existe).

### Ejemplo: crear colección con validación

```js
db.createCollection('producto', {
  validator: {
    $jsonSchema: {
      // ... schema de producto ...
    },
  },
  validationLevel: 'strict',
  validationAction: 'error',
});
```

### Ejemplo: modificar colección existente

```js
db.runCommand({
  collMod: 'producto',
  validator: {
    $jsonSchema: {
      // ... schema de producto ...
    },
  },
  validationLevel: 'strict',
  validationAction: 'error',
});
```

- `validationLevel: "strict"` -> valida todos los inserts/updates.
- `validationAction: "error"` -> rechaza documentos inválidos.
- `validationLevel: "moderate"` -> solo valida documentos que ya cumplían el schema previamente.

---

## Esquemas por colección

### 1. `project_user`

Entidad: `User` (extiende `AbstractAuditingEntity<String>`)

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["login", "password", "activated"],
    "properties": {
      "_id": {
        "bsonType": ["objectId", "string"],
        "description": "Identificador único del usuario"
      },
      "login": {
        "bsonType": "string",
        "minLength": 1,
        "maxLength": 50,
        "pattern": "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$",
        "description": "Login único del usuario (email o nombre de usuario)"
      },
      "password": {
        "bsonType": "string",
        "minLength": 60,
        "maxLength": 60,
        "description": "Hash BCrypt de la contraseña"
      },
      "first_name": {
        "bsonType": "string",
        "maxLength": 50
      },
      "last_name": {
        "bsonType": "string",
        "maxLength": 50
      },
      "email": {
        "bsonType": "string",
        "minLength": 5,
        "maxLength": 254
      },
      "activated": {
        "bsonType": "bool"
      },
      "lang_key": {
        "bsonType": "string",
        "minLength": 2,
        "maxLength": 10
      },
      "image_url": {
        "bsonType": "string",
        "maxLength": 256
      },
      "activation_key": {
        "bsonType": "string",
        "maxLength": 20
      },
      "reset_key": {
        "bsonType": "string",
        "maxLength": 20
      },
      "reset_date": {
        "bsonType": "date"
      },
      "authorities": {
        "bsonType": "array",
        "items": {
          "bsonType": "object",
          "required": ["$id"],
          "properties": {
            "$ref": { "bsonType": "string" },
            "$id": { "bsonType": ["objectId", "string"] },
            "$db": { "bsonType": "string" }
          }
        }
      },
      "created_by": { "bsonType": "string" },
      "created_date": { "bsonType": "date" },
      "last_modified_by": { "bsonType": "string" },
      "last_modified_date": { "bsonType": "date" }
    }
  }
}
```

---

### 2. `project_authority`

Entidad: `Authority`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["_id"],
    "properties": {
      "_id": {
        "bsonType": "string",
        "maxLength": 50,
        "description": "Nombre del rol (ej: ROLE_USER, ROLE_ADMIN)"
      }
    }
  }
}
```

---

### 3. `categoria`

Entidad: `Categoria`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["nombre", "slug", "activo"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "nombre": {
        "bsonType": "string",
        "maxLength": 100
      },
      "slug": {
        "bsonType": "string",
        "maxLength": 120
      },
      "descripcion": {
        "bsonType": "string"
      },
      "imagen": {
        "bsonType": "binData"
      },
      "imagen_content_type": {
        "bsonType": "string"
      },
      "activo": {
        "bsonType": "bool"
      }
    }
  }
}
```

---

### 4. `subcategoria`

Entidad: `Subcategoria`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["nombre", "slug", "activo"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "nombre": {
        "bsonType": "string",
        "maxLength": 100
      },
      "slug": {
        "bsonType": "string",
        "maxLength": 120
      },
      "descripcion": {
        "bsonType": "string"
      },
      "imagen": {
        "bsonType": "binData"
      },
      "imagen_content_type": {
        "bsonType": "string"
      },
      "activo": {
        "bsonType": "bool"
      },
      "categoria": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["categoria"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 5. `marca`

Entidad: `Marca`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["nombre", "slug"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "nombre": {
        "bsonType": "string",
        "maxLength": 100
      },
      "slug": {
        "bsonType": "string",
        "maxLength": 120
      }
    }
  }
}
```

---

### 6. `categoriaiva`

Entidad: `CategoriaIVA`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["nombre", "porcentaje", "estado"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "nombre": {
        "bsonType": "string",
        "maxLength": 60
      },
      "porcentaje": {
        "bsonType": "decimal",
        "minimum": 0,
        "maximum": 100
      },
      "estado": {
        "bsonType": "string",
        "enum": ["ACTIVO", "INACTIVO"]
      }
    }
  }
}
```

---

### 7. `producto`

Entidad: `Producto`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["nombre", "slug", "sku", "destacado", "activo"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "nombre": {
        "bsonType": "string",
        "maxLength": 200
      },
      "slug": {
        "bsonType": "string",
        "maxLength": 220
      },
      "referencia": {
        "bsonType": "string",
        "maxLength": 60
      },
      "sku": {
        "bsonType": "string",
        "maxLength": 100
      },
      "color": {
        "bsonType": "string",
        "maxLength": 50
      },
      "talla": {
        "bsonType": "string",
        "maxLength": 30
      },
      "codigo_barras": {
        "bsonType": "string",
        "maxLength": 50
      },
      "unidad_medida": {
        "bsonType": "string",
        "maxLength": 20
      },
      "descripcion": {
        "bsonType": "string"
      },
      "destacado": {
        "bsonType": "bool"
      },
      "activo": {
        "bsonType": "bool"
      },
      "precio": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto_precio"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "inventario": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto_inventario"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "imagenes": {
        "bsonType": "array",
        "items": {
          "bsonType": "object",
          "required": ["$id"],
          "properties": {
            "$ref": { "bsonType": "string", "enum": ["producto_imagen"] },
            "$id": { "bsonType": ["objectId", "string"] },
            "$db": { "bsonType": "string" }
          }
        }
      },
      "categoria": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["categoria"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "subcategoria": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["subcategoria"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "marca": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["marca"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "categoriaIva": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["categoriaiva"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 8. `producto_precio`

Entidad: `ProductoPrecio`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["precio_compra", "precio_venta"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "precio_compra": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "precio_venta": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "precio_adicional": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "ganancia": {
        "bsonType": "decimal"
      },
      "producto": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 9. `producto_inventario`

Entidad: `ProductoInventario`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["stock"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "stock": {
        "bsonType": "int",
        "minimum": 0
      },
      "stock_minimo": {
        "bsonType": "int",
        "minimum": 0
      },
      "ubicacion_bodega": {
        "bsonType": "string",
        "enum": ["BODEGA_PRINCIPAL", "BODEGA_SECUNDARIA", "BODEGA_NORTE", "BODEGA_SUR", "EXHIBICION", "CONSIGNACION"]
      },
      "garantia_meses": {
        "bsonType": "int",
        "minimum": 0
      },
      "producto": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 10. `producto_imagen`

Entidad: `ProductoImagen`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["imagen_content_type", "es_principal"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "imagen": {
        "bsonType": "binData"
      },
      "imagen_content_type": {
        "bsonType": "string"
      },
      "imagen_alt": {
        "bsonType": "string",
        "maxLength": 200
      },
      "es_principal": {
        "bsonType": "bool"
      },
      "producto": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 11. `etiqueta_producto`

Entidad: `EtiquetaProducto`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["etiqueta"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "etiqueta": {
        "bsonType": "string",
        "maxLength": 80
      },
      "producto": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 12. `cuenta`

Entidad: `Cuenta`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["primer_nombre", "primer_apellido", "activo"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "num_documento": {
        "bsonType": "string",
        "maxLength": 20
      },
      "primer_nombre": {
        "bsonType": "string",
        "maxLength": 50
      },
      "segundo_nombre": {
        "bsonType": "string",
        "maxLength": 50
      },
      "primer_apellido": {
        "bsonType": "string",
        "maxLength": 50
      },
      "segundo_apellido": {
        "bsonType": "string",
        "maxLength": 50
      },
      "genero": {
        "bsonType": "string",
        "enum": ["MASCULINO", "FEMENINO", "PREFIERO_NO_DECIR"]
      },
      "fecha_nacimiento": {
        "bsonType": "date"
      },
      "celular": {
        "bsonType": "string",
        "maxLength": 15
      },
      "telefono": {
        "bsonType": "string",
        "maxLength": 15
      },
      "foto_perfil": {
        "bsonType": "binData"
      },
      "foto_perfil_content_type": {
        "bsonType": "string"
      },
      "activo": {
        "bsonType": "bool"
      },
      "user": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["project_user"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "tipoDocumento": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["tipo_documento"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 13. `tipo_documento`

Entidad: `TipoDocumento`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["sigla", "nombre_tipo", "estado"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "sigla": {
        "bsonType": "string",
        "maxLength": 10
      },
      "nombre_tipo": {
        "bsonType": "string",
        "maxLength": 60
      },
      "estado": {
        "bsonType": "string",
        "enum": ["ACTIVO", "INACTIVO"]
      }
    }
  }
}
```

---

### 14. `direccion`

Entidad: `Direccion`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["direccion", "municipio", "departamento", "activo"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "direccion": {
        "bsonType": "string",
        "maxLength": 100
      },
      "barrio": {
        "bsonType": "string",
        "maxLength": 100
      },
      "localidad": {
        "bsonType": "string",
        "maxLength": 100
      },
      "municipio": {
        "bsonType": "string",
        "maxLength": 100
      },
      "departamento": {
        "bsonType": "string",
        "maxLength": 100
      },
      "activo": {
        "bsonType": "bool"
      },
      "cuenta": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["cuenta"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "pedido": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["pedido"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 15. `carrito`

Entidad: `Carrito`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "subtotal": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "fecha_actualizacion": {
        "bsonType": "date"
      },
      "cuenta": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["cuenta"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 16. `item_carrito`

Entidad: `ItemCarrito`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["cantidad", "precio_unitario"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "cantidad": {
        "bsonType": "int",
        "minimum": 1
      },
      "precio_unitario": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "subtotal": {
        "bsonType": "decimal"
      },
      "carrito": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["carrito"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "producto": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 17. `pedido`

Entidad: `Pedido`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["numero_pedido", "estado", "subtotal", "total"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "numero_pedido": {
        "bsonType": "string",
        "maxLength": 30
      },
      "estado": {
        "bsonType": "string",
        "enum": ["PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED"]
      },
      "subtotal": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "descuento": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "iva_total": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "costo_envio": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "total": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "notas_cliente": {
        "bsonType": "string",
        "maxLength": 500
      },
      "notas_internas": {
        "bsonType": "string",
        "maxLength": 500
      },
      "ip_origen": {
        "bsonType": "string",
        "maxLength": 45
      },
      "user_agent": {
        "bsonType": "string",
        "maxLength": 300
      },
      "direccion": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["direccion"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "cuenta": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["cuenta"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "envio": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["envio"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 18. `item_pedido`

Entidad: `ItemPedido`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["nombre_producto", "cantidad", "precio_unitario"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "nombre_producto": {
        "bsonType": "string",
        "maxLength": 200
      },
      "slug_producto": {
        "bsonType": "string",
        "maxLength": 220
      },
      "marca_producto": {
        "bsonType": "string",
        "maxLength": 100
      },
      "sku_producto": {
        "bsonType": "string",
        "maxLength": 100
      },
      "color_producto": {
        "bsonType": "string",
        "maxLength": 50
      },
      "talla_producto": {
        "bsonType": "string",
        "maxLength": 30
      },
      "cantidad": {
        "bsonType": "int",
        "minimum": 1
      },
      "precio_unitario": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "porcentaje_iva": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "valor_iva": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "descuento": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "subtotal": {
        "bsonType": "decimal"
      },
      "pedido": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["pedido"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      },
      "producto": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["producto"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 19. `pago`

Entidad: `Pago`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["metodo_pago", "estado", "monto"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "metodo_pago": {
        "bsonType": "string",
        "enum": ["CREDIT_CARD", "DEBIT_CARD", "PSE", "CASH", "NEQUI", "DAVIPLATA", "EFECTY", "CONTRA_ENTREGA"]
      },
      "estado": {
        "bsonType": "string",
        "enum": ["PENDING", "APPROVED", "REJECTED", "REFUNDED", "EXPIRED", "CANCELLED"]
      },
      "monto": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "referencia_pasarela": {
        "bsonType": "string",
        "maxLength": 200
      },
      "codigo_autorizacion": {
        "bsonType": "string",
        "maxLength": 100
      },
      "descripcion_respuesta": {
        "bsonType": "string",
        "maxLength": 300
      },
      "intentos": {
        "bsonType": "int",
        "minimum": 0
      },
      "fecha_pago": {
        "bsonType": "date"
      },
      "pedido": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["pedido"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 20. `factura`

Entidad: `Factura`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["subtotal", "total", "enviada"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "prefijo": {
        "bsonType": "string",
        "maxLength": 10
      },
      "cufe": {
        "bsonType": "string",
        "maxLength": 96
      },
      "subtotal": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "descuentos": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "base_gravable_iva": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "valor_iva": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "total": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "notas_adicionales": {
        "bsonType": "string",
        "maxLength": 500
      },
      "codigo_qr": {
        "bsonType": "string"
      },
      "enviada": {
        "bsonType": "bool"
      },
      "fecha_emision": {
        "bsonType": "date"
      },
      "fecha_vencimiento": {
        "bsonType": "date"
      },
      "fecha_envio_email": {
        "bsonType": "date"
      },
      "pago": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["pago"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

### 21. `envio`

Entidad: `Envio`

```json
{
  "$jsonSchema": {
    "bsonType": "object",
    "required": ["estado"],
    "properties": {
      "_id": { "bsonType": ["objectId", "string"] },
      "transportadora": {
        "bsonType": "string",
        "maxLength": 100
      },
      "numero_rastreo": {
        "bsonType": "string",
        "maxLength": 100
      },
      "tipo_servicio": {
        "bsonType": "string",
        "enum": ["ESTANDAR", "EXPRESS", "MISMO_DIA", "PROGRAMADO", "PUNTO_PICKUP"]
      },
      "estado": {
        "bsonType": "string",
        "enum": ["PENDING", "DISPATCHED", "IN_TRANSIT", "IN_CITY", "DELIVERED", "RETURNED", "LOST"]
      },
      "costo_envio": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "peso_kg": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "valor_declarado": {
        "bsonType": "decimal",
        "minimum": 0
      },
      "url_rastreo": {
        "bsonType": "string",
        "maxLength": 300
      },
      "observaciones": {
        "bsonType": "string",
        "maxLength": 300
      },
      "fecha_despacho": {
        "bsonType": "date"
      },
      "fecha_entrega_estimada": {
        "bsonType": "date"
      },
      "fecha_entrega": {
        "bsonType": "date"
      },
      "pedido": {
        "bsonType": "object",
        "required": ["$id"],
        "properties": {
          "$ref": { "bsonType": "string", "enum": ["pedido"] },
          "$id": { "bsonType": ["objectId", "string"] },
          "$db": { "bsonType": "string" }
        }
      }
    }
  }
}
```

---

## Recomendaciones adicionales

1. **Aplicar primero en un ambiente de pruebas** antes de ejecutar en producción, ya que `validationAction: "error"` rechazará documentos existentes que no cumplan el schema al momento de actualizarlos.
2. **Validar el formato real de los enums** en la base de datos. Si el proyecto usa converters de Spring Data que almacenan `getValue()` en lugar de `name()`, los arrays `enum` deben actualizarse.
3. **Índices únicos:** Considerar crear índices únicos en campos como `login` y `email` de `project_user`, `slug` de `categoria`/`subcategoria`/`marca`/`producto`, `sku` de `producto`, `numero_pedido` de `pedido`, etc.
4. **DBRefs:** Si el proyecto está configurado para almacenar referencias como simples `ObjectId` (sin objeto `$ref`/`$db`), los schemas de referencias deben simplificarse a `{ "bsonType": ["objectId", "string"] }`.
5. **Auditoría:** Los campos `created_date` y `last_modified_date` se inicializan automáticamente por Spring Data; no es necesario enviarlos desde el cliente.

---

## Generación masiva

Para aplicar todos los schemas de una sola vez, puedes utilizar un script de shell con `mongosh` o cargar un archivo `.js` que ejecute los comandos `db.createCollection`/`collMod` para cada colección listada arriba.

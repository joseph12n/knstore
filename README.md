# KN-Store — E-commerce Platform

![Status](https://img.shields.io/badge/Status-En_Desarrollo-blue)
![Backend](https://img.shields.io/badge/Backend-Spring_Boot-green?logo=springboot)
![Frontend](https://img.shields.io/badge/Frontend-React_&_TS-blue?logo=react)
![Database](https://img.shields.io/badge/Database-MongoDB-darkgreen?logo=mongodb)

**KN-Store** es una plataforma de comercio electrónico integral diseñada para centralizar, automatizar y optimizar la gestión comercial de la tienda. El proyecto nace como una solución estratégica para migrar de procesos manuales e informales —como la recepción de pedidos fragmentada por WhatsApp— hacia un ecosistema digital integrado, eficiente y seguro.

---

## 📌 Contexto del Proyecto

### Planteamiento del Problema

Actualmente, la tienda enfrenta cuellos de botella operativos debido a la gestión manual de inventarios y ventas. La dependencia de canales de mensajería informal limita el alcance del negocio, genera demoras en la atención al cliente y produce inconsistencias en los registros de stock en tiempo real.

> **Pregunta Problema:** ¿Cómo la implementación de un sistema de comercio electrónico puede impulsar la transformación digital de la tienda KN_STORE, mejorando la atención al cliente y garantizando un control eficiente de los procesos internos?

### Justificación

La automatización integral de los procesos a través de **KN-Store** mitiga el error humano, optimiza los tiempos de respuesta y expande el alcance comercial de la marca. Al unificar el control de stock y ofrecer una experiencia de compra interactiva, el sistema proporciona datos precisos para la toma de decisiones y eleva la competitividad del negocio en el mercado digital.

---

## 🎯 Objetivos

### Objetivo General

Desarrollar un software (e-commerce) para la tienda **KN_STORE** que permita gestionar de manera eficiente los procesos de ventas, inventario y usuarios, mediante módulos integrados que faciliten la administración interna y mejoren la experiencia de compra del cliente, asegurando un entorno funcional, accesible y seguro.

### Objetivos Específicos

- **Diseño Arquitectónico:** Estructurar el sistema definiendo los módulos y funcionalidades necesarias para la gestión integral de la tienda.
- **Canal de Venta Digital:** Implementar una tienda online que permita a los clientes visualizar productos, realizar pedidos y efectuar compras de forma sencilla y segura.
- **Control Centralizado:** Desarrollar módulos de administración que gestionen los usuarios y el inventario en tiempo real, optimizando el control interno.
- **Seguridad y Accesibilidad:** Incorporar mecanismos de seguridad y roles de acceso que garanticen la protección de la información y el uso adecuado del sistema.

---

## 📦 Alcance del Sistema (Módulos Principales)

El sistema se compone de los siguientes bloques fundamentales para agilizar la operación:

- **🛒 Módulo de Visualización de Productos:** Catálogo digital interactivo donde los clientes exploran productos, filtran por atributos específicos y gestionan su flujo de compra de manera sencilla.
- **📦 Módulo de Control de Inventario:** Panel administrativo enfocado en el calzado, permitiendo el seguimiento y supervisión detallada del stock según **color, talla, marca y referencia**.
- **🚚 Módulo de Distribución de Productos:** Gestión del proceso de entrega. Permite a los clientes seleccionar entre modalidades como _Servicio Contraentrega_ o _Paquetería Convencional_ según su ubicación.
- **🛠️ Módulo de Servicios:** Herramienta interna para estructurar, visualizar y gestionar de forma organizada los distintos servicios complementarios de la tienda.
- **📧 Módulo de Notificaciones (SMTP):** Servicio automatizado para el envío de correos electrónicos transaccionales (confirmaciones de compra, restablecimiento de contraseñas y actualizaciones de estado).

---

## 🛠️ Stack Tecnológico

La plataforma está construida bajo una arquitectura desacoplada utilizando tecnologías modernas y de alto rendimiento:

- **Frontend:** `React.js` con `TypeScript`, garantizando interfaces dinámicas, reactivas y un desarrollo seguro gracias al tipado estático.
- **Backend:** `Java` con `Spring Boot`, proporcionando un núcleo robusto, escalable y eficiente para la lógica de negocio.
- **Base de Datos:** `MongoDB`, base de datos NoSQL orientada a documentos que permite una gestión flexible del catálogo de productos y registros dinámicos.
- **Servicio de Correo:** Protocolo `SMTP` integrado en el backend para la automatización de comunicaciones salientes directas al cliente.

---

## 📐 Arquitectura y Patrones de Diseño

El backend implementa principios de arquitectura limpia para asegurar la mantenibilidad del código:

- **Separación de Capas:** División estricta entre Controladores (REST APIs), Capa de Servicio (Lógica de negocio) y Repositorios (`Spring Data MongoDB`).
- **Patrón DTO (Data Transfer Objects):** Toda la comunicación entre el frontend y el backend se gestiona mediante DTOs.
  - **Desacoplamiento:** Desvincula los contratos de la API del modelo de almacenamiento de la base de datos.
  - **Seguridad:** Protege la información sensible (como contraseñas hash o metadatos innecesarios) evitando su exposición en la red.

---

## 🔐 Control de Acceso (Roles)

Para garantizar la integridad de las operaciones, el sistema restringe las funcionalidades mediante tres perfiles de usuario:

1. **Administrador:** Acceso total al sistema, gestión global de usuarios, configuraciones críticas del negocio y auditoría.
2. **Manager:** Gestión operativa del inventario, actualización de stock físico, administración de los servicios y control del estado de distribución.
3. **Cliente:** Navegación interactiva del catálogo, gestión de perfil personal, generación de pedidos y recepción de notificaciones vía e-mail.

# Pejibayera_del_Este
Proyecto Final del curso Desarrollo de Aplicaciones Web y Patrones de la Universidad Fidélitas (SC-403) IIIC 2026

# Qué hace el proyecto?
El proyecto consiste en un sitio web para una tienda digital de pejibayes, donde los usuarios podrán conocer la empresa, hacer consultas del catálogo de productos y realizar compras mediante un carrito.

 Roles del sistema:
   -Invitado (no tiene cuenta registrada)
   -Cliente (tiene una cuenta registrada "Individual")
   -Empresa (tiene una cuenta registra "Organización")
   -Administrador (empleado directo de la empresa)
   -SuperAdministrador (dueño de la empresa)
   
Funciones del sistema:
  -Registro e Inicio de Sesión
  -Cierre de Sesión
  -Autenticación y Autorización
  -Consulta de Catálogo (información:precio, nombre, cantidad)
  -Gestión de Pedidos (realizar pedido, eliminar pedido, modificar pedido, cronograma de pedidos)
  -Gestión de Carrito (cálculo del total, checkout, medio de pago)
  -Actualización de datos de usuario (modificar nombre, dirección, teléfono, correo)
  -Historial de Pedidos (visualizar pedidos y sus estados)
  -Gestión de Inventario (agregar producto, eliminar producto y modificar producto)
  -Gestión del Sistema (visualizar métricas del sistema y asignación de administradores)

Permisos del sistema por rol:
  1. Invitados
        Los invitados pueden visualizar la página normalmente, ver información de la empresa y consultar la información de los productos. Sin embargo, no puede realizar varias funcionalidades del sistema, primero se debe registrar e iniciar sesión.
  2. Cliente y Empresa
        Cliente y Empresa pueden iniciar y cerrar sesión, consultar productos, gestionar pedidos y carrito, realizar compras, actualizar su información y visualizar sus pedidos.
       Diferencia
         -Empresa puede realizar pedidos con una cantidad 50<x<150
         -Cliente realiza pedidos con una cantidad 0<x<50
         -Empresa puede realizar pedidos con un periodo de tiempo
  3. Administrador
         Realiza la gestión del inventario
  4. SuperAdministrador
        Realiza la gestión del sistema

Tecnologías utilizadas
  -NetBeans (IDE)
  -Java 21
  -Git y Github
  -HTML y CSS
  -Spring Boot
  -Thymeleaf
  -Bootstrap
  -Hibernate/JPA
  -Bases de Datos
    
# Cuál es la utilidad del proyecto?
  Beneficios para los usuarios:
    1. Conocer la empresa: Los visitantes pueden acceder a información general sobre la tienda sin necesidad de registrarse.
    2. Consultar el catálogo: Cualquier usuario puede ver los productos disponibles con su precio, nombre y cantidad.
    3. Comprar en línea: Clientes y empresas pueden armar un carrito, calcular el total, elegir medio de pago y realizar pedidos desde la web.
    4. Gestión de pedidos: Permite crear, modificar, eliminar y programar pedidos, así como consultar el historial y el estado de cada uno.
    5. Gestión de cuenta: Los usuarios registrados pueden actualizar sus datos personales (nombre, dirección, teléfono, correo).
  
  Utilidad para la empresa:
    1. Gestión de inventario: El administrador puede agregar, eliminar y modificar productos de forma ágil.
    2. Gestión del sistema: El superadministrador puede visualizar métricas del sistema y asignar administradores, lo que facilita el control y la toma de decisiones.
    
En resumen, el proyecto optimiza las operaciones de la tienda, reduce la dependencia de procesos manuales y ofrece un canal de venta accesible y organizado tanto para clientes individuales como para empresas.

# Contribuidores del proyecto
  -MENDOZA AGUILERA CHRISTOPHER JOSUÉ
  -DURAN OBANDO LEILANI AMARIS
  -LEITON BENAVIDES IGNACIO RODOLFO
  -ZURITA CUADROS ALEJANDRO

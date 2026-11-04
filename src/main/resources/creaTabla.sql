CREATE DATABASE IF NOT EXISTS pejibayera_del_este
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE pejibayera_del_este;

CREATE TABLE empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    correo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(250),
    estado TINYINT(1) NOT NULL DEFAULT 1,

    UNIQUE (correo)
);


CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(250),

    UNIQUE (nombre)
);


CREATE TABLE permiso (
    id_permiso INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(250),

    UNIQUE (nombre)
);


CREATE TABLE rol_permiso (
    id_rol INT NOT NULL,
    id_permiso INT NOT NULL,

    PRIMARY KEY (id_rol, id_permiso),

    FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol),

    FOREIGN KEY (id_permiso)
        REFERENCES permiso(id_permiso)
);


CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NULL,
    id_rol INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    estado TINYINT(1) NOT NULL DEFAULT 1,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (correo),

    FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa),

    FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol)
);

CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(250),

    FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    id_categoria INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen VARCHAR(1024),
    estado TINYINT(1) NOT NULL DEFAULT 1,

    FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa),

    FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria),

    CHECK (precio >= 0),
    CHECK (stock >= 0)
);

CREATE TABLE carrito (
    id_carrito INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'Activo',

    UNIQUE (id_usuario),

    FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
);


CREATE TABLE detalle_carrito (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_carrito INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,

    UNIQUE (id_carrito, id_producto),

    FOREIGN KEY (id_carrito)
        REFERENCES carrito(id_carrito),

    FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto),

    CHECK (cantidad > 0),
    CHECK (precio >= 0)
);


CREATE TABLE pedido (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_empresa INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',

    FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),

    FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa),

    CHECK (total >= 0)
);

CREATE TABLE detalle_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,

    FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido),

    FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto),

    CHECK (cantidad > 0),
    CHECK (precio_unitario >= 0),
    CHECK (subtotal >= 0)
);


INSERT INTO rol (nombre, descripcion) VALUES
('Superadministrador', 'Administra toda la plataforma'),
('Administrador', 'Administra una empresa'),
('Cliente', 'Realiza compras y consulta pedidos');

INSERT INTO permiso (nombre, descripcion) VALUES
('Administrar empresas', 'Registrar y gestionar empresas'),
('Administrar usuarios', 'Gestionar usuarios del sistema'),
('Administrar roles', 'Gestionar roles y permisos'),
('Administrar productos', 'Registrar y gestionar productos'),
('Administrar categorias', 'Gestionar categorias'),
('Administrar perfil', 'Consultar y actualizar perfil'),
('Consultar pedidos', 'Consultar pedidos'),
('Cambiar estado pedidos', 'Actualizar estado de pedidos'),
('Realizar compras', 'Crear pedidos y compras'),
('Consultar catalogo', 'Consultar productos disponibles');

-- Superadministrador: todos los permisos
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso
FROM permiso;


-- Administrador: permisos de gestión empresarial
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 2, id_permiso
FROM permiso
WHERE nombre IN (
    'Administrar usuarios',
    'Administrar productos',
    'Administrar categorias',
    'Administrar perfil',
    'Consultar pedidos',
    'Cambiar estado pedidos',
    'Realizar compras',
    'Consultar catalogo'
);

-- Cliente: permisos de compra y perfil
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 3, id_permiso
FROM permiso
WHERE nombre IN (
    'Administrar perfil',
    'Consultar pedidos',
    'Realizar compras',
    'Consultar catalogo'
);

CREATE USER 'pejibayera_app'@'localhost'
IDENTIFIED BY 'shelosXD11'; -- con esto sirve para meter a la base cada integrante

GRANT SELECT, INSERT, UPDATE, DELETE
ON pejibayera_del_este.*
TO 'pejibayera_app'@'localhost';

USE pejibayera_del_este;
-- Registrar empresa principal
INSERT INTO empresa
(nombre, descripcion, correo, telefono, direccion, estado)
VALUES
(
    'Pejibayera del Este',
    'Empresa dedicada a la comercialización de pejibayes.',
    'pejibayeraEste@correo.com',
    '8888-4444',
    'Cartago, Costa Rica',
    1
);

SET @id_empresa = LAST_INSERT_ID();

-- Registrar categoría
INSERT INTO categoria
(id_empresa, nombre, descripcion)
VALUES
(
    @id_empresa,
    'Pejibayes',
    'Productos frescos y preparados de pejibaye.'
);

-- Productos
USE pejibayera_del_este; 

INSERT INTO producto
(id_empresa, id_categoria, nombre, descripcion, precio, stock, imagen, estado)
VALUES
(
    1,
    1,
    'Pejibaye fresco por kilo',
    'Pejibayes frescos seleccionados.',
    1500.00,
    30,
    NULL,
    1
),
(
    1,
    1,
    'Pejibaye cocinado por kilo',
    'Pejibayes cocinados listos para consumir.',
    2500.00,
    20,
    NULL,
    1
),
(
    1,
    1,
    'Combo familiar de pejibayes',
    'Paquete familiar de pejibayes.',
    4500.00,
    15,
    NULL,
    1
);

INSERT INTO producto
(id_empresa, id_categoria, nombre, descripcion, precio, stock, imagen, estado)
VALUES
(
    1,
    1,
    'Harina de pejibaye',
    'Harina elaborada a partir de pejibayes seleccionados.',
    3000.00,
    20,
    NULL,
    1
),
(
    1,
    1,
    'Queque de pejibaye',
    'Delicioso queque preparado con pejibaye.',
    5000.00,
    10,
    NULL,
    1
);

-- Comprobar registros
SELECT * FROM producto;
SELECT * FROM empresa;
SELECT * FROM categoria;
SELECT * FROM producto;

-- Esta consulta le pregunta a MySQL:
SELECT COUNT(*) AS total_productos
FROM producto;

SELECT COUNT(*) AS total_empresas
FROM empresa;

SELECT COUNT(*) AS total_categorias
FROM categoria;

-- Aqui se verifican  los datos iniciales que insertamos en nuestro SQL.
SELECT COUNT(*) AS total_roles FROM rol;   
SELECT COUNT(*) AS total_permisos FROM permiso;
SELECT COUNT(*) AS total_relaciones FROM rol_permiso;

USE pejibayera_del_este;

SELECT
    id_usuario,
    nombre,
    apellido,
    correo,
    telefono,
    estado,
    id_rol,
    id_empresa
FROM usuario
ORDER BY id_usuario;

-- Eliminar las cuentas de todos los clientes
DELETE u
FROM usuario u
INNER JOIN rol r ON u.id_rol = r.id_rol
WHERE r.nombre = 'Cliente';

SELECT COUNT(*) AS clientes_restantes
FROM usuario u
INNER JOIN rol r ON u.id_rol = r.id_rol
WHERE r.nombre = 'Cliente';


USE pejibayera_del_este;

SELECT
    (SELECT COUNT(*) FROM empresa) AS empresas,
    (SELECT COUNT(*) FROM categoria) AS categorias,
    (SELECT COUNT(*) FROM producto) AS productos;


SELECT
    id_detalle,
    id_carrito,
    id_producto,
    cantidad,
    precio
FROM detalle_carrito;

USE pejibayera_del_este;

SELECT
    dp.id_detalle,
    dp.id_pedido,
    p.nombre,
    dp.cantidad,
    dp.precio_unitario,
    dp.subtotal
FROM detalle_pedido dp
INNER JOIN producto p
    ON dp.id_producto = p.id_producto;
    
USE pejibayera_del_este;
SET SQL_SAFE_UPDATES = 0;

-- 1. Eliminar los detalles de los pedidos
DELETE FROM detalle_pedido;

-- 2. Eliminar todos los pedidos
DELETE FROM pedido;

-- 3. Eliminar los productos de los carritos
DELETE FROM detalle_carrito;

-- 4. Eliminar los carritos
DELETE FROM carrito;

-- 5. Eliminar todos los usuarios
DELETE FROM usuario;
    
    
SELECT COUNT(*) AS total_usuarios FROM usuario;
SELECT COUNT(*) AS total_pedidos FROM pedido;
SELECT COUNT(*) AS total_detalles_pedido FROM detalle_pedido;
SELECT COUNT(*) AS total_carritos FROM carrito;
SELECT COUNT(*) AS total_detalles_carrito FROM detalle_carrito;

SELECT COUNT(*) AS usuarios FROM usuario;
SELECT COUNT(*) AS pedidos FROM pedido;
SELECT COUNT(*) AS detalle_pedido FROM detalle_pedido;
SELECT COUNT(*) AS carritos FROM carrito;
SELECT COUNT(*) AS detalle_carrito FROM detalle_carrito;


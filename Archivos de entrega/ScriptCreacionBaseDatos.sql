-- Creacion de base de datos
CREATE SCHEMA ProductoCodigoBarra;
USE ProductoCodigoBarra;
-- Eliminar tablas existentes
DROP TABLE IF EXISTS Producto;
DROP TABLE IF EXISTS CodigoBarras;

-- Crear tablas
CREATE TABLE CodigoBarras(
  id BIGINT PRIMARY KEY,
  eliminado BOOLEAN DEFAULT FALSE,
  tipo VARCHAR(5) NOT NULL CHECK (tipo IN ('EAN13','EAN8','UPC')),
  valor VARCHAR(20) NOT NULL UNIQUE,
  fechaAsignacion DATE,
  observaciones VARCHAR(255),
  CHECK((tipo = 'EAN13' AND CHAR_LENGTH(valor) = 13) 
	OR(tipo = 'EAN8' AND CHAR_LENGTH(valor) = 8)
    OR(tipo = 'UPC' AND CHAR_LENGTH(valor) = 12))
);

CREATE TABLE Producto(
   id BIGINT PRIMARY KEY,
  eliminado BOOLEAN DEFAULT FALSE,
  nombre VARCHAR(120) NOT NULL,
  marca VARCHAR(80),
  categoria VARCHAR(80),
  precio DECIMAL(10,2) NOT NULL CHECK(precio > 0),
  peso DECIMAL(10,3) CHECK(peso > 0),
  codigoBarras BIGINT UNIQUE NOT NULL,
  CONSTRAINT fk_codigo FOREIGN KEY (codigoBarras) REFERENCES CodigoBarras(id)
	ON DELETE RESTRICT
    ON UPDATE RESTRICT
);

-- Insert de datos 
SET @rownum := 0;
INSERT INTO CodigoBarras (id, tipo, valor, fechaAsignacion, observaciones)
SELECT
  (@rownum := @rownum + 1) AS id,
  CASE
    WHEN MOD(@rownum,10) < 6 THEN 'EAN13'
    WHEN MOD(@rownum,10) < 9 THEN 'UPC'
    ELSE 'EAN8'
  END AS tipo,
    CASE 
	WHEN MOD(@rownum,10) < 6 then LPAD(@rownum,13,'0') 
	WHEN MOD(@rownum,10) < 9 then LPAD(@rownum,12,'0') 
    ELSE LPAD(@rownum, 8, '0')
  END AS valor,
  DATE_ADD('2022-01-01', INTERVAL (@rownum MOD 1000) DAY) AS fechaAsignacion,
  IF(MOD(@rownum,10)=0, CONCAT('Obs auto generado para cb_',@rownum), NULL)
FROM
  (
    SELECT 0
    FROM information_schema.columns c1,
         information_schema.columns c2
    LIMIT 30
  ) AS gen;

-- Generar 30 filas en Producto
INSERT INTO Producto (id, nombre, marca, categoria, precio, peso, codigoBarras)
SELECT
  id,
  CONCAT(ELT(MOD(id,7)+1, 'Mouse','Teclado','Auriculares','Plato','Auto de juguete','Robot de juguete','Mouse de juguete'), ' ', id),
  ELT(MOD(id,7)+1, 'Sony','Samsung','LG','Philips','Xiaomi','Bose','JBL'),
  ELT(MOD(id,8)+1, 'Electrónica','Hogar','Deporte','Juguetes','Alimentos','Bebidas','Limpieza','Computación'),
  ROUND(100 + (RAND()*1000), 2),
  ROUND(0.1 + (RAND()*5), 3),
  id codigobarras
FROM CodigoBarras;
-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jun 29, 2025 at 11:58 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `granturismo`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_estadisticas_blockchain` ()   BEGIN
    SELECT 
        'Total Artesanías' as concepto,
        COUNT(*) as cantidad
    FROM servicio_artesania
    
    UNION ALL
    
    SELECT 
        'Artesanías en Blockchain' as concepto,
        COUNT(*) as cantidad
    FROM servicio_artesania 
    WHERE hash_blockchain IS NOT NULL
    
    UNION ALL
    
    SELECT 
        'Transacciones Confirmadas' as concepto,
        COUNT(*) as cantidad
    FROM transaccion_blockchain_artesania 
    WHERE estado = 'CONFIRMADO'
    
    UNION ALL
    
    SELECT 
        'Transacciones Pendientes' as concepto,
        COUNT(*) as cantidad
    FROM transaccion_blockchain_artesania 
    WHERE estado = 'PENDIENTE'
    
    UNION ALL
    
    SELECT 
        'Transacciones Fallidas' as concepto,
        COUNT(*) as cantidad
    FROM transaccion_blockchain_artesania 
    WHERE estado = 'FALLIDO';
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `actividades`
--

CREATE TABLE `actividades` (
  `id_actividad` bigint NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `descripcion` text,
  `imagen_url` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) NOT NULL,
  `duracion_horas` int NOT NULL,
  `precio_base` decimal(38,2) NOT NULL,
  `imagen_public_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `actividades`
--

INSERT INTO `actividades` (`id_actividad`, `titulo`, `descripcion`, `imagen_url`, `tipo`, `duracion_horas`, `precio_base`, `imagen_public_id`) VALUES
(3, 'Tour en Bicicleta por la Ciudad', 'Explora los rincones históricos de la ciudad en un recorrido guiado en bicicleta. Incluye paradas en monumentos clave y degustación de comida local.', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1747691164/ixuay7gleibdbx5uz1sv.jpg', 'Cultural', 5, '55.99', 'ixuay7gleibdbx5uz1sv'),
(4, 'Aventura Urbana en Bicicleta', 'Un recorrido simulado en bicicleta por los puntos de interés más \"virtuales\" de la ciudad. Incluye paradas \"digitales\" en monumentos importantes y una \"degustación\" de comida pixelada. Ideal para probar la funcionalidad de la reserva sin salir de casa.', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1751053987/rmdxpgoewl7ynkf53jy3.webp', 'Simulación/Prueba', 2, '0.00', 'rmdxpgoewl7ynkf53jy3');

-- --------------------------------------------------------

--
-- Table structure for table `actividad_detalle`
--

CREATE TABLE `actividad_detalle` (
  `id_actividad_detalle` bigint NOT NULL,
  `id_actividad` bigint DEFAULT NULL,
  `id_paquete` bigint DEFAULT NULL,
  `titulo` varchar(255) NOT NULL,
  `descripcion` text NOT NULL,
  `imagen_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `orden` int NOT NULL,
  `imagen_public_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `actividad_detalle`
--

INSERT INTO `actividad_detalle` (`id_actividad_detalle`, `id_actividad`, `id_paquete`, `titulo`, `descripcion`, `imagen_url`, `orden`, `imagen_public_id`) VALUES
(7, 3, 10, 'Avistamiento de aves', 'Recorrido por zonas de observación de aves nativas.', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1747692318/w2y3pcejmhqs09yboipa.avif', 4, 'w2y3pcejmhqs09yboipa'),
(9, 3, 10, 'Caminata al Mirador', 'Subida guiada al punto más alto de la laguna.', 'https://example.com/mirador.jpg', 1, 'mirador_img'),
(10, 3, 10, 'Inmersión en las Cavernas Virtuales', 'Una experiencia de prueba para simular el descenso y exploración de misteriosas cavernas generadas por computadora. Ideal para evaluar la integración de sistemas y la carga de datos sin riesgos.', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1751054538/uy5slv3hbapiol8qm8fq.jpg', 4, 'uy5slv3hbapiol8qm8fq');

-- --------------------------------------------------------

--
-- Table structure for table `carrito`
--

CREATE TABLE `carrito` (
  `id_carrito` bigint NOT NULL,
  `estado` enum('CANCELADO','CONFIRMADO','PENDIENTE') NOT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  `id_usuario` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `carrito_item`
--

CREATE TABLE `carrito_item` (
  `id_carrito_item` bigint NOT NULL,
  `cantidad_personas` int DEFAULT NULL,
  `fecha_reserva` datetime(6) DEFAULT NULL,
  `notas` varchar(255) DEFAULT NULL,
  `referencia_id` bigint NOT NULL,
  `tipo` varchar(255) NOT NULL,
  `id_carrito` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `conversacion`
--

CREATE TABLE `conversacion` (
  `id` bigint NOT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `mensaje_usuario` varchar(255) DEFAULT NULL,
  `respuesta_bot` varchar(5000) DEFAULT NULL,
  `sesion_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `conversacion`
--

INSERT INTO `conversacion` (`id`, `fecha`, `mensaje_usuario`, `respuesta_bot`, `sesion_id`) VALUES
(1, '2025-06-28 00:45:39.124207', 'que paquetes ofrecen?', '¡Hola! ¡Gracias por preguntar sobre nuestros paquetes! Actualmente, tenemos dos opciones increíbles que te permitirán vivir experiencias únicas:\n\n*   **Aventura en Punta Sal:** ¿Te imaginas combinando la belleza de la playa con la emoción de la montaña? Nuestro paquete a Punta Sal te ofrece justo eso. Disfruta de actividades de senderismo con vistas espectaculares y siente la adrenalina del rappel. ¡Una aventura inolvidable por solo 1800.75 soles!\n\n*   **Relax en Laguna Azul:** Si lo que buscas es desconectar del estrés y recargar energías en un entorno natural impresionante, nuestro paquete a la Laguna Azul es perfecto para ti. Sumérgete en la tranquilidad de este paraíso y déjate envolver por su magia. ¡Todo esto por tan solo 850.00 soles!\n\n¿Cuál de estas experiencias te llama más la atención? ¡Estoy aquí para darte más detalles y ayudarte a planificar tu próxima aventura!\n', 1),
(2, '2025-06-28 00:48:22.136751', 'que servicios ofrecen?', '¡Descubre experiencias inolvidables con nuestros servicios! Te ofrecemos:\n\n*   **Taller de Artesanía:** Sumérgete en la tradición artesanal con un taller práctico donde crearás tu propia obra de arte. ¡Todos los materiales están incluidos!\n*   **Alimentación:** Disfruta de deliciosos platos y bebidas que complementarán tu aventura turística. ¡Tenemos paquetes para todos los gustos!\n*   **Guía Turístico:** Explora la riqueza histórica y cultural de la región con nuestros guías certificados. ¡Te contarán historias fascinantes en cada paso!\n*   **Hospedaje Rural:** Relájate y reconecta con la naturaleza en nuestro alojamiento rústico con impresionantes vistas a la laguna. ¡Un escape perfecto!\n\n¿Cuál de estos servicios te interesa más para ayudarte a planificar tu viaje?\n', 1),
(3, '2025-06-28 00:48:55.105073', 'quiero realizar una reserva', 'Entiendo que deseas realizar una reserva. Sin embargo, mi función es brindarte información sobre nuestros destinos turísticos, paquetes de viaje, servicios de hotelería, alimentación y artesanía disponibles en GranTurismo. No puedo realizar reservas directamente.\n\n¿En qué destino o paquete de viaje estás interesado para poder darte más información?\n', 1),
(4, '2025-06-29 23:41:09.849955', 'que paquetes ofrecen?', '¡Hola! ¡Gracias por preguntar por nuestros paquetes! Actualmente tenemos dos opciones increíbles para ti:\n\n*   **Aventura en Punta Sal:** Si buscas emociones fuertes y paisajes impresionantes, te encantará nuestro paquete a Punta Sal. Imagina senderismo en las montañas y la adrenalina del rappel con vistas espectaculares. ¡Todo esto por solo 1800.75 soles!\n\n*   **Relax en la Laguna Azul:** ¿Necesitas un respiro y reconectar con la naturaleza? Nuestro paquete a la Laguna Azul es perfecto para ti. Sumérgete en la tranquilidad de este paraíso natural por tan solo 850.00 soles.\n\n¿Cuál de estos destinos te llama más la atención? ¡Estoy aquí para ayudarte a planificar tus próximas vacaciones! 😊\n', 2);

-- --------------------------------------------------------

--
-- Table structure for table `destino`
--

CREATE TABLE `destino` (
  `id_destino` bigint NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` text,
  `ubicacion` varchar(255) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `latitud` double DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `popularidad` int DEFAULT NULL,
  `precio_medio` decimal(38,2) DEFAULT NULL,
  `rating` float DEFAULT NULL,
  `imagen_public_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `destino`
--

INSERT INTO `destino` (`id_destino`, `nombre`, `descripcion`, `ubicacion`, `imagen_url`, `latitud`, `longitud`, `popularidad`, `precio_medio`, `rating`, `imagen_public_id`) VALUES
(6, 'Playa de Punta Sal', 'Una hermosa playa en el norte de Perú, famosa por su agua cristalina y arenas blancas.', 'Tumbes, Perú', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1747690683/wdaabrynupaavt1grckr.jpg', -3.7583, -80.5472, 5, '120.50', 4.7, 'wdaabrynupaavt1grckr'),
(7, 'Laguna Azul', 'Hermoso lugar para relajarse y disfrutar de la naturaleza.', 'San Martín, Perú', 'https://example.com/laguna.jpg', -6.5246, -76.3524, 4, '95.00', 4.5, 'laguna_azul_img'),
(8, 'Machu Picchu', 'Antigua ciudad inca ubicada en lo alto de los Andes, famosa por su arquitectura intrincada y vistas impresionantes.', 'Cusco, Perú', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1748299731/qjg5jy723vsnbov2mjat.png', -13.1631, -72.545, 5, '80.00', 4.9, 'qjg5jy723vsnbov2mjat');

-- --------------------------------------------------------

--
-- Table structure for table `disponibilidad`
--

CREATE TABLE `disponibilidad` (
  `id_disponibilidad` bigint NOT NULL,
  `id_servicio` bigint DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `cantidad` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `favorito`
--

CREATE TABLE `favorito` (
  `id_favorito` bigint NOT NULL,
  `referencia_id` bigint NOT NULL,
  `tipo` varchar(255) NOT NULL,
  `id_usuario` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `favorito`
--

INSERT INTO `favorito` (`id_favorito`, `referencia_id`, `tipo`, `id_usuario`) VALUES
(1, 10, 'paquete', 43),
(2, 5, 'servicioArtesania', 43);

-- --------------------------------------------------------

--
-- Table structure for table `paquetes`
--

CREATE TABLE `paquetes` (
  `id_paquete` bigint NOT NULL,
  `titulo` varchar(150) NOT NULL,
  `descripcion` text,
  `imagen_url` varchar(255) DEFAULT NULL,
  `precio_total` decimal(38,2) NOT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `duracion_dias` int DEFAULT NULL,
  `localidad` varchar(255) NOT NULL,
  `tipo_actividad` varchar(255) NOT NULL,
  `cupos_maximos` int DEFAULT NULL,
  `fecha_inicio` datetime(6) NOT NULL,
  `fecha_fin` datetime(6) NOT NULL,
  `id_proveedor` bigint DEFAULT NULL,
  `id_destino` bigint DEFAULT NULL,
  `imagen_public_id` varchar(255) DEFAULT NULL,
  `idioma_original` varchar(5) DEFAULT NULL,
  `moneda_original` varchar(3) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `paquetes`
--

INSERT INTO `paquetes` (`id_paquete`, `titulo`, `descripcion`, `imagen_url`, `precio_total`, `estado`, `duracion_dias`, `localidad`, `tipo_actividad`, `cupos_maximos`, `fecha_inicio`, `fecha_fin`, `id_proveedor`, `id_destino`, `imagen_public_id`, `idioma_original`, `moneda_original`) VALUES
(10, 'Paquete Vacacional de Aventura en la Montaña', 'Vive la aventura en las montañas con actividades de senderismo y rappel.', 'https://res.cloudinary.com/ddkdsx3fg/image/upload/v1747690757/gubstsdpbq3wahnxzotq.png', '1800.75', 'DISPONIBLE', 6, 'Cerro Alto', 'Aventura', 40, '2025-08-01 08:00:00.000000', '2025-08-07 08:00:00.000000', 3, 6, 'gubstsdpbq3wahnxzotq', NULL, NULL),
(12, 'Escapada Natural a la Laguna', 'Un paquete perfecto para desconectar en la naturaleza.', 'https://example.com/escapada.jpg', '850.00', 'DISPONIBLE', 3, 'Laguna Azul', 'Relax', 20, '2025-09-15 09:00:00.000000', '2025-09-18 09:00:00.000000', 7, 7, 'escapada_natural_img', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `paquete_detalle`
--

CREATE TABLE `paquete_detalle` (
  `id_paquete_detalle` bigint NOT NULL,
  `id_paquete` bigint DEFAULT NULL,
  `id_servicio` bigint DEFAULT NULL,
  `cantidad` int NOT NULL,
  `precio_especial` decimal(38,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `paquete_detalle`
--

INSERT INTO `paquete_detalle` (`id_paquete_detalle`, `id_paquete`, `id_servicio`, `cantidad`, `precio_especial`) VALUES
(3, 12, 6, 1, '180.00');

-- --------------------------------------------------------

--
-- Table structure for table `preferences`
--

CREATE TABLE `preferences` (
  `id_preference` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id_usuario` bigint NOT NULL,
  `preferred_currency_code` varchar(3) NOT NULL,
  `preferred_language_code` varchar(5) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `preferences`
--

INSERT INTO `preferences` (`id_preference`, `created_at`, `id_usuario`, `preferred_currency_code`, `preferred_language_code`, `updated_at`) VALUES
(1, '2025-06-11 11:51:23.908925', 43, 'EUR', 'en', '2025-06-11 11:51:23.908925'),
(3, '2025-06-29 23:52:00.473628', 69, 'PEN', 'es', '2025-06-29 23:52:00.473628'),
(4, '2025-06-29 23:53:42.614224', 70, 'PEN', 'es', '2025-06-29 23:53:42.614224'),
(5, '2025-06-29 23:55:13.250490', 71, 'PEN', 'es', '2025-06-29 23:55:13.250490'),
(6, '2025-06-29 23:57:36.691472', 72, 'PEN', 'es', '2025-06-29 23:57:36.691472'),
(7, '2025-06-29 23:58:30.017556', 73, 'PEN', 'es', '2025-06-29 23:58:30.017556');

-- --------------------------------------------------------

--
-- Table structure for table `proveedor`
--

CREATE TABLE `proveedor` (
  `id_proveedor` bigint NOT NULL,
  `nombre_completo` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telefono` varchar(10) NOT NULL,
  `fecha_registro` datetime(6) NOT NULL,
  `id_usuario` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `proveedor`
--

INSERT INTO `proveedor` (`id_proveedor`, `nombre_completo`, `email`, `telefono`, `fecha_registro`, `id_usuario`) VALUES
(3, 'NateRiver', 'nicksaim69@gmail.com', '958787651', '2025-04-20 23:36:43.000000', 43),
(6, 'Luis Rey', 'luis.rey@example.com', '987654321', '2025-05-01 11:15:00.000000', 44),
(7, 'María López', 'maria.lopez@example.com', '912345678', '2025-04-21 10:00:00.000000', 48);

-- --------------------------------------------------------

--
-- Table structure for table `puntos`
--

CREATE TABLE `puntos` (
  `id` bigint NOT NULL,
  `uid_usuario` bigint NOT NULL,
  `cantidad_puntos` int NOT NULL,
  `tipo_transaccion` varchar(50) NOT NULL,
  `fecha_transaccion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `referencia_id` bigint DEFAULT NULL,
  `descripcion` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `puntos`
--

INSERT INTO `puntos` (`id`, `uid_usuario`, `cantidad_puntos`, `tipo_transaccion`, `fecha_transaccion`, `referencia_id`, `descripcion`) VALUES
(1, 43, 100, 'REGISTRO', '2025-06-20 21:46:58', NULL, 'Puntos de bienvenida por registro (otorgado manualmente)'),
(2, 61, 100, 'REGISTRO', '2025-06-20 21:58:46', NULL, 'Puntos de bienvenida por registro de cuenta'),
(3, 61, 20, 'COMPARTIR_APP', '2025-06-20 22:01:55', NULL, 'Puntos por compartir la aplicación'),
(4, 61, 50, 'RESERVA_CONFIRMADA', '2025-06-20 22:04:11', 3, 'Puntos por reserva #3 confirmada.'),
(5, 61, -30, 'CANJE', '2025-06-20 22:06:30', NULL, 'Canje de prueba'),
(6, 62, 100, 'REGISTRO', '2025-06-23 04:48:46', NULL, 'Puntos de bienvenida por registro de cuenta'),
(7, 63, 100, 'REGISTRO', '2025-06-23 06:35:08', NULL, 'Puntos de bienvenida por registro de cuenta'),
(8, 64, 100, 'REGISTRO', '2025-06-23 06:41:03', NULL, 'Puntos de bienvenida por registro de cuenta'),
(9, 65, 100, 'REGISTRO', '2025-06-23 06:50:13', NULL, 'Puntos de bienvenida por registro de cuenta'),
(10, 43, 20, 'COMPARTIR_APP', '2025-06-23 06:56:59', NULL, 'Puntos por compartir la aplicación'),
(11, 66, 100, 'REGISTRO', '2025-06-28 22:03:45', NULL, 'Puntos de bienvenida por registro de cuenta'),
(12, 67, 100, 'REGISTRO', '2025-06-28 22:19:48', NULL, 'Puntos de bienvenida por registro de cuenta');

-- --------------------------------------------------------

--
-- Table structure for table `resenas`
--

CREATE TABLE `resenas` (
  `id_resena` bigint NOT NULL,
  `id_usuario` bigint DEFAULT NULL,
  `id_paquete` bigint DEFAULT NULL,
  `calificacion` int DEFAULT NULL,
  `comentario` text,
  `fecha` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `resenas`
--

INSERT INTO `resenas` (`id_resena`, `id_usuario`, `id_paquete`, `calificacion`, `comentario`, `fecha`) VALUES
(7, 48, 10, 5, 'Una experiencia inolvidable en la laguna.', '2025-09-19 10:30:00.000000');

-- --------------------------------------------------------

--
-- Table structure for table `reservas`
--

CREATE TABLE `reservas` (
  `id_reserva` bigint NOT NULL,
  `id_usuario` bigint DEFAULT NULL,
  `fecha_inicio` datetime DEFAULT NULL,
  `fecha_fin` datetime DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `cantidad_personas` int DEFAULT NULL,
  `id_paquete` bigint DEFAULT NULL,
  `observaciones` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reservas`
--

INSERT INTO `reservas` (`id_reserva`, `id_usuario`, `fecha_inicio`, `fecha_fin`, `estado`, `cantidad_personas`, `id_paquete`, `observaciones`) VALUES
(2, 48, '2025-09-15 09:00:00', '2025-09-18 09:00:00', 'CONFIRMADA', 2, 10, '');

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id_rol` bigint NOT NULL,
  `nombre` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `descripcion` varchar(120) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id_rol`, `nombre`, `descripcion`) VALUES
(1, 'ADMIN', 'Administrador'),
(2, 'PROV', 'Proveedor'),
(3, 'USER', 'Usuario');

-- --------------------------------------------------------

--
-- Table structure for table `servicios`
--

CREATE TABLE `servicios` (
  `id_servicio` bigint NOT NULL,
  `id_tipo` bigint DEFAULT NULL,
  `nombre_servicio` varchar(255) DEFAULT NULL,
  `descripcion` text,
  `precio_base` decimal(38,2) NOT NULL,
  `estado` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `servicios`
--

INSERT INTO `servicios` (`id_servicio`, `id_tipo`, `nombre_servicio`, `descripcion`, `precio_base`, `estado`) VALUES
(3, 3, 'Taller de Artesanía', 'Experiencia artesanal auténtica con materiales incluidos.', '100.00', 'ACTIVO'),
(4, 2, 'Alimentación', 'Servicios de alimentación y bebidas en paquetes turísticos.', '150.00', 'ACTIVO'),
(5, 2, 'Guía Turístico', 'Acompañamiento y explicación de sitios históricos por un guía certificado.', '80.00', 'ACTIVO'),
(6, 4, 'Hospedaje Rural', 'Alojamiento rústico con vista a la laguna.', '200.00', 'ACTIVO');

-- --------------------------------------------------------

--
-- Table structure for table `servicio_alimentacion`
--

CREATE TABLE `servicio_alimentacion` (
  `id_alimentacion` bigint NOT NULL,
  `id_servicio` bigint DEFAULT NULL,
  `tipo_comida` varchar(255) DEFAULT NULL,
  `estilo_gastronomico` varchar(255) NOT NULL,
  `incluye_bebidas` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `servicio_alimentacion`
--

INSERT INTO `servicio_alimentacion` (`id_alimentacion`, `id_servicio`, `tipo_comida`, `estilo_gastronomico`, `incluye_bebidas`) VALUES
(2, 4, 'Buffet Internacional', 'Fusión Peruana', 'SI');

-- --------------------------------------------------------

--
-- Table structure for table `servicio_artesania`
--

CREATE TABLE `servicio_artesania` (
  `id_artesania` bigint NOT NULL,
  `id_servicio` bigint DEFAULT NULL,
  `tipo_artesania` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `nivel_dificultad` varchar(255) DEFAULT NULL,
  `duracion_taller` int DEFAULT NULL,
  `incluye_material` tinyint(1) DEFAULT NULL,
  `artesania` text,
  `origen_cultural` varchar(255) DEFAULT NULL,
  `max_participantes` int DEFAULT NULL,
  `visita_taller` tinyint(1) DEFAULT NULL,
  `artesano` varchar(255) NOT NULL,
  `hash_blockchain` varchar(66) DEFAULT NULL COMMENT 'Hash SHA-256 de la transacción en blockchain (64 chars + 0x prefix)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `servicio_artesania`
--

INSERT INTO `servicio_artesania` (`id_artesania`, `id_servicio`, `tipo_artesania`, `nivel_dificultad`, `duracion_taller`, `incluye_material`, `artesania`, `origen_cultural`, `max_participantes`, `visita_taller`, `artesano`, `hash_blockchain`) VALUES
(2, 4, 'Cerámica Andina', 'INTERMEDIO', 120, 1, 'Taller de modelado y pintura de cerámica tradicional.', 'Cusco', 15, 1, 'Juan Pérez', '0x39241f4519d739eb711df00134b722aad58952528abc46eb2493611d2b6dda7c'),
(3, 3, 'Cerámica Andina', 'INTERMEDIO', 120, 1, 'Taller de modelado y pintura de cerámica tradicional.', 'Cusco', 15, 1, 'Juan Pérez', '0x63d0a954a50ce1cf3c4a09ee2ed52266f6b643033283b26e3710737061597eeb'),
(4, 4, 'Cerámica Andina', 'INTERMEDIO', 120, 1, 'Taller de modelado y pintura de cerámica tradicional.', 'Cusco', 15, 1, 'Juan Pérez', NULL),
(5, 3, 'Cerámica Tradicional', 'INTERMEDIO', 120, 1, 'Taller de cerámica tradicional andina con técnicas ancestrales transmitidas de generación en generación', 'Cultura Inca', 8, 1, 'María Condori Mamani', '0x573650adfca0254d98b785385479d0fa9f7268dd03c8d73c8da4d8321587e2fd'),
(6, 3, 'Cerámica con Cloudinary', 'INTERMEDIO', 120, 1, 'Prueba de QR almacenado en Cloudinary', 'Cultura Test', 8, 1, 'Artesano Test Cloudinary', '0x99c98beb17816f48d3213dfc1cfd5927189cd70c70c38ac76f85ac50b3402287');

-- --------------------------------------------------------

--
-- Table structure for table `servicio_hotelera`
--

CREATE TABLE `servicio_hotelera` (
  `id_hoteleria` bigint NOT NULL,
  `id_servicio` bigint DEFAULT NULL,
  `tipo_habitacion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `estrellas` int DEFAULT NULL,
  `incluye_desayuno` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `max_personas` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `servicio_hotelera`
--

INSERT INTO `servicio_hotelera` (`id_hoteleria`, `id_servicio`, `tipo_habitacion`, `estrellas`, `incluye_desayuno`, `max_personas`) VALUES
(1, 4, 'Suite Deluxe', 5, 'Si', 4);

-- --------------------------------------------------------

--
-- Table structure for table `sesion_chat`
--

CREATE TABLE `sesion_chat` (
  `id` bigint NOT NULL,
  `inicio` datetime(6) DEFAULT NULL,
  `ultima_interaccion` datetime(6) DEFAULT NULL,
  `usuario_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `sesion_chat`
--

INSERT INTO `sesion_chat` (`id`, `inicio`, `ultima_interaccion`, `usuario_id`) VALUES
(1, '2025-06-28 00:38:03.895445', '2025-06-28 00:48:54.191477', 43),
(2, '2025-06-29 23:41:04.548396', '2025-06-29 23:41:04.549898', 67);

-- --------------------------------------------------------

--
-- Table structure for table `tipo_servicio`
--

CREATE TABLE `tipo_servicio` (
  `id_tipo` bigint NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tipo_servicio`
--

INSERT INTO `tipo_servicio` (`id_tipo`, `nombre`, `descripcion`) VALUES
(1, 'Aventura', 'Servicios relacionados con actividades al aire libre, como senderismo, rafting y tours extremos.'),
(2, 'Alimentación', 'Servicios de alimentación y bebidas en paquetes turísticos.'),
(3, 'Cultural', 'Servicios relacionados a talleres y expresiones culturales.'),
(4, 'Alojamiento', 'Servicios de hospedaje para turistas.');

-- --------------------------------------------------------

--
-- Table structure for table `transaccion_blockchain_artesania`
--

CREATE TABLE `transaccion_blockchain_artesania` (
  `id` bigint NOT NULL,
  `id_artesania` bigint NOT NULL COMMENT 'FK manual a servicio_artesania.id_artesania',
  `hash_transaccion` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Hash único de la transacción en blockchain',
  `url_explorer` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL completa para ver la transacción en el explorador',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Estado de la transacción: PENDIENTE, CONFIRMADO, FALLIDO',
  `gas_usado` bigint DEFAULT NULL COMMENT 'Cantidad de gas consumido en la transacción',
  `costo_transaccion` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Costo total de la transacción en Wei o la unidad correspondiente'
) ;

--
-- Dumping data for table `transaccion_blockchain_artesania`
--

INSERT INTO `transaccion_blockchain_artesania` (`id`, `id_artesania`, `hash_transaccion`, `url_explorer`, `fecha_registro`, `estado`, `gas_usado`, `costo_transaccion`) VALUES
(1, 5, '0x573650adfca0254d98b785385479d0fa9f7268dd03c8d73c8da4d8321587e2fd', 'https://sepolia.etherscan.io/tx/0x573650adfca0254d98b785385479d0fa9f7268dd03c8d73c8da4d8321587e2fd', '2025-06-21 23:43:15', 'CONFIRMADO', NULL, NULL),
(2, 6, '0x99c98beb17816f48d3213dfc1cfd5927189cd70c70c38ac76f85ac50b3402287', 'https://sepolia.etherscan.io/tx/0x99c98beb17816f48d3213dfc1cfd5927189cd70c70c38ac76f85ac50b3402287', '2025-06-27 19:39:03', 'CONFIRMADO', NULL, NULL),
(3, 2, '0x39241f4519d739eb711df00134b722aad58952528abc46eb2493611d2b6dda7c', 'https://sepolia.etherscan.io/tx/0x39241f4519d739eb711df00134b722aad58952528abc46eb2493611d2b6dda7c', '2025-06-29 05:02:55', 'CONFIRMADO', NULL, NULL),
(4, 3, '0x63d0a954a50ce1cf3c4a09ee2ed52266f6b643033283b26e3710737061597eeb', 'https://sepolia.etherscan.io/tx/0x63d0a954a50ce1cf3c4a09ee2ed52266f6b643033283b26e3710737061597eeb', '2025-06-29 05:04:55', 'CONFIRMADO', NULL, NULL);

--
-- Triggers `transaccion_blockchain_artesania`
--
DELIMITER $$
CREATE TRIGGER `trg_update_artesania_blockchain` AFTER INSERT ON `transaccion_blockchain_artesania` FOR EACH ROW BEGIN
    -- Actualizar el hash en servicio_artesania si no existe
    UPDATE servicio_artesania 
    SET hash_blockchain = NEW.hash_transaccion
    WHERE id_artesania = NEW.id_artesania 
    AND (hash_blockchain IS NULL OR hash_blockchain = '');
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `clave` varchar(100) NOT NULL,
  `verificado` enum('NO','SI') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `email`, `clave`, `verificado`) VALUES
(43, 'Nate@gmail.com', '$2a$12$vj9XcNYudmVDwAKaQCQGzeV1ZQjBHqrx3oRCAALqEfdfxp1BIDDUK', 'SI'),
(44, 'Nick@gmail.com', '$2a$10$WPpeczgEPds8fs1vAV5wSue7d7vdzeJXT/RVkaNulOlMFw.xCbciK', 'NO'),
(48, 'NickS@gmail.com', '$2a$10$cA05fjtwyhkqBlEKvPxtk.ZqKOVNKtANpaGCbF9hlt9jcLX.Lq20S', 'SI'),
(67, 'nicksaim69@gmail.com', '$2a$10$HIJvmhDGmeHAkC9FqeCTp.xeKM3mQ9076qDQOKFSAEA5U/LMLfotq', 'SI'),
(69, 'davidnina658@gmail.com', '$2a$10$7K352.bXRPYWAZeXuME.Suc3qXxq4pbAlF3rgolbTjAnYEKHPElpC', 'SI'),
(70, 'henyel91@gmail.com', '$2a$10$doFGDh2hTpHxyA7vdBir.uMU7Dh5pMara2KLNR9C9t1lKPfGw/tba', 'SI'),
(71, 'jhanramos8888@gmail.com', '$2a$10$ED.SqkpHk/LL07GpWMZ0TeFB1ZoW2Nvmceb6CTxNk7zyk5kF23gei', 'SI'),
(72, 'valente@gmail.com', '$2a$10$syFbo/75RTxptyon/8ZScOO.h7dMIHWbw5OS7SNT1N2HxP2zpuNcW', 'SI'),
(73, 'rev15cpr@gmail.com', '$2a$10$u0ebUeQA4jW/8Tqgs9wo1uBsvI3kE5XGwG5DuGFau/C.WtLHZV4cG', 'SI');

-- --------------------------------------------------------

--
-- Table structure for table `usuario_rol`
--

CREATE TABLE `usuario_rol` (
  `usuario_id` bigint NOT NULL,
  `rol_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `usuario_rol`
--

INSERT INTO `usuario_rol` (`usuario_id`, `rol_id`) VALUES
(43, 1),
(44, 1),
(48, 1),
(69, 1),
(70, 1),
(71, 1),
(72, 1),
(73, 2);

-- --------------------------------------------------------

--
-- Table structure for table `verification_token`
--

CREATE TABLE `verification_token` (
  `id` bigint NOT NULL,
  `fecha_expiracion` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `usuario_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_artesania_blockchain`
-- (See below for the actual view)
--
CREATE TABLE `vw_artesania_blockchain` (
`artesano` varchar(255)
,`estado_blockchain` varchar(20)
,`estado_registro` varchar(12)
,`fecha_registro_blockchain` datetime
,`hash_blockchain` varchar(66)
,`id_artesania` bigint
,`origen_cultural` varchar(255)
,`tipo_artesania` varchar(255)
,`url_explorer` varchar(255)
);

-- --------------------------------------------------------

--
-- Structure for view `vw_artesania_blockchain`
--
DROP TABLE IF EXISTS `vw_artesania_blockchain`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_artesania_blockchain`  AS SELECT `sa`.`id_artesania` AS `id_artesania`, `sa`.`tipo_artesania` AS `tipo_artesania`, `sa`.`artesano` AS `artesano`, `sa`.`origen_cultural` AS `origen_cultural`, `sa`.`hash_blockchain` AS `hash_blockchain`, `tba`.`estado` AS `estado_blockchain`, `tba`.`fecha_registro` AS `fecha_registro_blockchain`, `tba`.`url_explorer` AS `url_explorer`, (case when (`sa`.`hash_blockchain` is not null) then 'REGISTRADO' else 'SIN_REGISTRO' end) AS `estado_registro` FROM (`servicio_artesania` `sa` left join `transaccion_blockchain_artesania` `tba` on((`sa`.`id_artesania` = `tba`.`id_artesania`))) ORDER BY `sa`.`id_artesania` ASC  ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `actividades`
--
ALTER TABLE `actividades`
  ADD PRIMARY KEY (`id_actividad`);

--
-- Indexes for table `actividad_detalle`
--
ALTER TABLE `actividad_detalle`
  ADD PRIMARY KEY (`id_actividad_detalle`),
  ADD KEY `id_actividad` (`id_actividad`),
  ADD KEY `id_paquete` (`id_paquete`);

--
-- Indexes for table `carrito`
--
ALTER TABLE `carrito`
  ADD PRIMARY KEY (`id_carrito`),
  ADD KEY `FK_CARRITO_USUARIO` (`id_usuario`);

--
-- Indexes for table `carrito_item`
--
ALTER TABLE `carrito_item`
  ADD PRIMARY KEY (`id_carrito_item`),
  ADD KEY `FK_CARRITOITEM_CARRITO` (`id_carrito`);

--
-- Indexes for table `conversacion`
--
ALTER TABLE `conversacion`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKlur3gji236eoku53hm7xpcd9q` (`sesion_id`);

--
-- Indexes for table `destino`
--
ALTER TABLE `destino`
  ADD PRIMARY KEY (`id_destino`);

--
-- Indexes for table `disponibilidad`
--
ALTER TABLE `disponibilidad`
  ADD PRIMARY KEY (`id_disponibilidad`),
  ADD KEY `id_servicio` (`id_servicio`);

--
-- Indexes for table `favorito`
--
ALTER TABLE `favorito`
  ADD PRIMARY KEY (`id_favorito`),
  ADD KEY `FK_USUARIO_FAVORITO` (`id_usuario`);

--
-- Indexes for table `paquetes`
--
ALTER TABLE `paquetes`
  ADD PRIMARY KEY (`id_paquete`),
  ADD KEY `id_destino` (`id_destino`),
  ADD KEY `FK_PAQUETE_PROVEEDOR` (`id_proveedor`);

--
-- Indexes for table `paquete_detalle`
--
ALTER TABLE `paquete_detalle`
  ADD PRIMARY KEY (`id_paquete_detalle`),
  ADD KEY `id_paquete` (`id_paquete`),
  ADD KEY `id_servicio` (`id_servicio`);

--
-- Indexes for table `preferences`
--
ALTER TABLE `preferences`
  ADD PRIMARY KEY (`id_preference`),
  ADD UNIQUE KEY `UK3ncj5eb26scmbxq5v58kxipi2` (`id_usuario`);

--
-- Indexes for table `proveedor`
--
ALTER TABLE `proveedor`
  ADD PRIMARY KEY (`id_proveedor`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indexes for table `puntos`
--
ALTER TABLE `puntos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_uid_usuario` (`uid_usuario`),
  ADD KEY `idx_tipo_transaccion` (`tipo_transaccion`),
  ADD KEY `idx_fecha_transaccion` (`fecha_transaccion`);

--
-- Indexes for table `resenas`
--
ALTER TABLE `resenas`
  ADD PRIMARY KEY (`id_resena`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_paquete` (`id_paquete`);

--
-- Indexes for table `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id_reserva`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_paquete` (`id_paquete`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id_rol`);

--
-- Indexes for table `servicios`
--
ALTER TABLE `servicios`
  ADD PRIMARY KEY (`id_servicio`),
  ADD KEY `id_tipo` (`id_tipo`);

--
-- Indexes for table `servicio_alimentacion`
--
ALTER TABLE `servicio_alimentacion`
  ADD PRIMARY KEY (`id_alimentacion`),
  ADD KEY `id_servicio` (`id_servicio`);

--
-- Indexes for table `servicio_artesania`
--
ALTER TABLE `servicio_artesania`
  ADD PRIMARY KEY (`id_artesania`),
  ADD KEY `id_servicio` (`id_servicio`),
  ADD KEY `idx_servicio_artesania_hash_blockchain` (`hash_blockchain`);

--
-- Indexes for table `servicio_hotelera`
--
ALTER TABLE `servicio_hotelera`
  ADD PRIMARY KEY (`id_hoteleria`),
  ADD KEY `id_servicio` (`id_servicio`);

--
-- Indexes for table `sesion_chat`
--
ALTER TABLE `sesion_chat`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tipo_servicio`
--
ALTER TABLE `tipo_servicio`
  ADD PRIMARY KEY (`id_tipo`);

--
-- Indexes for table `transaccion_blockchain_artesania`
--
ALTER TABLE `transaccion_blockchain_artesania`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_hash_transaccion` (`hash_transaccion`),
  ADD KEY `idx_id_artesania` (`id_artesania`),
  ADD KEY `idx_hash_transaccion` (`hash_transaccion`),
  ADD KEY `idx_estado` (`estado`),
  ADD KEY `idx_fecha_registro` (`fecha_registro`);

--
-- Indexes for table `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `UKnglinedpa99qfym8jreg27mx8` (`email`);

--
-- Indexes for table `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD PRIMARY KEY (`rol_id`,`usuario_id`),
  ADD KEY `FKehuc4c37b7soxfqfexqh3kg6s` (`usuario_id`);

--
-- Indexes for table `verification_token`
--
ALTER TABLE `verification_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKp678btf3r9yu6u8aevyb4ff0m` (`token`),
  ADD UNIQUE KEY `UKkupmakr7cygmw4w6da45egbo0` (`usuario_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `actividades`
--
ALTER TABLE `actividades`
  MODIFY `id_actividad` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `actividad_detalle`
--
ALTER TABLE `actividad_detalle`
  MODIFY `id_actividad_detalle` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `carrito`
--
ALTER TABLE `carrito`
  MODIFY `id_carrito` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `carrito_item`
--
ALTER TABLE `carrito_item`
  MODIFY `id_carrito_item` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `conversacion`
--
ALTER TABLE `conversacion`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `destino`
--
ALTER TABLE `destino`
  MODIFY `id_destino` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `disponibilidad`
--
ALTER TABLE `disponibilidad`
  MODIFY `id_disponibilidad` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `favorito`
--
ALTER TABLE `favorito`
  MODIFY `id_favorito` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `paquetes`
--
ALTER TABLE `paquetes`
  MODIFY `id_paquete` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `paquete_detalle`
--
ALTER TABLE `paquete_detalle`
  MODIFY `id_paquete_detalle` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `preferences`
--
ALTER TABLE `preferences`
  MODIFY `id_preference` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `proveedor`
--
ALTER TABLE `proveedor`
  MODIFY `id_proveedor` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `puntos`
--
ALTER TABLE `puntos`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `resenas`
--
ALTER TABLE `resenas`
  MODIFY `id_resena` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id_reserva` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id_rol` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `servicios`
--
ALTER TABLE `servicios`
  MODIFY `id_servicio` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `servicio_alimentacion`
--
ALTER TABLE `servicio_alimentacion`
  MODIFY `id_alimentacion` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `servicio_artesania`
--
ALTER TABLE `servicio_artesania`
  MODIFY `id_artesania` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `servicio_hotelera`
--
ALTER TABLE `servicio_hotelera`
  MODIFY `id_hoteleria` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `sesion_chat`
--
ALTER TABLE `sesion_chat`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tipo_servicio`
--
ALTER TABLE `tipo_servicio`
  MODIFY `id_tipo` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `transaccion_blockchain_artesania`
--
ALTER TABLE `transaccion_blockchain_artesania`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT for table `verification_token`
--
ALTER TABLE `verification_token`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `actividad_detalle`
--
ALTER TABLE `actividad_detalle`
  ADD CONSTRAINT `actividad_detalle_ibfk_1` FOREIGN KEY (`id_actividad`) REFERENCES `actividades` (`id_actividad`),
  ADD CONSTRAINT `actividad_detalle_ibfk_2` FOREIGN KEY (`id_paquete`) REFERENCES `paquetes` (`id_paquete`);

--
-- Constraints for table `carrito`
--
ALTER TABLE `carrito`
  ADD CONSTRAINT `FK_CARRITO_USUARIO` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Constraints for table `carrito_item`
--
ALTER TABLE `carrito_item`
  ADD CONSTRAINT `FK_CARRITOITEM_CARRITO` FOREIGN KEY (`id_carrito`) REFERENCES `carrito` (`id_carrito`);

--
-- Constraints for table `conversacion`
--
ALTER TABLE `conversacion`
  ADD CONSTRAINT `FKlur3gji236eoku53hm7xpcd9q` FOREIGN KEY (`sesion_id`) REFERENCES `sesion_chat` (`id`);

--
-- Constraints for table `disponibilidad`
--
ALTER TABLE `disponibilidad`
  ADD CONSTRAINT `disponibilidad_ibfk_1` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id_servicio`);

--
-- Constraints for table `favorito`
--
ALTER TABLE `favorito`
  ADD CONSTRAINT `FK_USUARIO_FAVORITO` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Constraints for table `paquetes`
--
ALTER TABLE `paquetes`
  ADD CONSTRAINT `paquetes_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  ADD CONSTRAINT `paquetes_ibfk_2` FOREIGN KEY (`id_destino`) REFERENCES `destino` (`id_destino`);

--
-- Constraints for table `paquete_detalle`
--
ALTER TABLE `paquete_detalle`
  ADD CONSTRAINT `paquete_detalle_ibfk_1` FOREIGN KEY (`id_paquete`) REFERENCES `paquetes` (`id_paquete`),
  ADD CONSTRAINT `paquete_detalle_ibfk_2` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id_servicio`);

--
-- Constraints for table `preferences`
--
ALTER TABLE `preferences`
  ADD CONSTRAINT `FKj4rgmkplxtx1f3v5xosa6qmfn` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Constraints for table `proveedor`
--
ALTER TABLE `proveedor`
  ADD CONSTRAINT `proveedor_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Constraints for table `resenas`
--
ALTER TABLE `resenas`
  ADD CONSTRAINT `resenas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `resenas_ibfk_2` FOREIGN KEY (`id_paquete`) REFERENCES `paquetes` (`id_paquete`);

--
-- Constraints for table `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`id_paquete`) REFERENCES `paquetes` (`id_paquete`);

--
-- Constraints for table `servicios`
--
ALTER TABLE `servicios`
  ADD CONSTRAINT `servicios_ibfk_2` FOREIGN KEY (`id_tipo`) REFERENCES `tipo_servicio` (`id_tipo`);

--
-- Constraints for table `servicio_alimentacion`
--
ALTER TABLE `servicio_alimentacion`
  ADD CONSTRAINT `servicio_alimentacion_ibfk_1` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id_servicio`);

--
-- Constraints for table `servicio_artesania`
--
ALTER TABLE `servicio_artesania`
  ADD CONSTRAINT `servicio_artesania_ibfk_1` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id_servicio`);

--
-- Constraints for table `servicio_hotelera`
--
ALTER TABLE `servicio_hotelera`
  ADD CONSTRAINT `servicio_hotelera_ibfk_1` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id_servicio`);

--
-- Constraints for table `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD CONSTRAINT `FKbyfgloj439r9wr9smrms9u33r` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `FKe3kd49gu3mhj2ty5kl44qsytp` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id_rol`);

--
-- Constraints for table `verification_token`
--
ALTER TABLE `verification_token`
  ADD CONSTRAINT `FK1q3cp6xcfpkb7v2bi2iktdqy0` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

INSERT INTO recetas (nombre, descripcion, tiempo_minutos, dificultad, tipo_alimentacion)
SELECT 'Arroz con pollo', 'Plato casero y reconfortante', 45, 'MEDIA', 'OMNIVORO'
WHERE NOT EXISTS (SELECT 1 FROM recetas WHERE nombre = 'Arroz con pollo');
INSERT INTO recetas (nombre, descripcion, tiempo_minutos, dificultad, tipo_alimentacion)
SELECT 'Ensalada Cesar vegetariana', 'Ensalada fresca con aderezo cremoso', 20, 'FACIL', 'VEGETARIANO'
WHERE NOT EXISTS (SELECT 1 FROM recetas WHERE nombre = 'Ensalada Cesar vegetariana');
INSERT INTO recetas (nombre, descripcion, tiempo_minutos, dificultad, tipo_alimentacion)
SELECT 'Spaghetti con salsa de tomate', 'Pasta sencilla con tomate y albahaca', 30, 'FACIL', 'VEGANO'
WHERE NOT EXISTS (SELECT 1 FROM recetas WHERE nombre = 'Spaghetti con salsa de tomate');
INSERT INTO recetas (nombre, descripcion, tiempo_minutos, dificultad, tipo_alimentacion)
SELECT 'Sopa de lentejas', 'Sopa caliente, nutritiva y reconfortante', 30, 'FACIL', 'VEGANO'
WHERE NOT EXISTS (SELECT 1 FROM recetas WHERE nombre = 'Sopa de lentejas');
INSERT INTO recetas (nombre, descripcion, tiempo_minutos, dificultad, tipo_alimentacion)
SELECT 'Bowl de quinoa y garbanzos', 'Bowl fresco con proteina vegetal', 25, 'FACIL', 'VEGANO'
WHERE NOT EXISTS (SELECT 1 FROM recetas WHERE nombre = 'Bowl de quinoa y garbanzos');

UPDATE recetas SET publica = true WHERE publica IS NULL;
UPDATE recetas SET porciones = 2 WHERE porciones IS NULL;
UPDATE recetas SET tipo_receta = CASE
  WHEN nombre = 'Sopa de lentejas' THEN 'SOPA'
  WHEN nombre LIKE 'Ensalada%' THEN 'ENTRANTE'
  ELSE 'PLATO_FUERTE'
END WHERE tipo_receta IS NULL;

INSERT INTO ingredientes_receta (receta_id, ingrediente)
SELECT r.id, i.ingrediente
FROM recetas r
CROSS JOIN (VALUES ('pasta'), ('tomate'), ('albahaca')) AS i(ingrediente)
WHERE r.nombre = 'Spaghetti con salsa de tomate'
  AND NOT EXISTS (
    SELECT 1 FROM ingredientes_receta ir
    WHERE ir.receta_id = r.id AND ir.ingrediente = i.ingrediente
  );
INSERT INTO ingredientes_receta (receta_id, ingrediente)
SELECT r.id, i.ingrediente
FROM recetas r
CROSS JOIN (VALUES ('lentejas'), ('zanahoria'), ('cebolla')) AS i(ingrediente)
WHERE r.nombre = 'Sopa de lentejas'
  AND NOT EXISTS (
    SELECT 1 FROM ingredientes_receta ir
    WHERE ir.receta_id = r.id AND ir.ingrediente = i.ingrediente
  );
INSERT INTO ingredientes_receta (receta_id, ingrediente)
SELECT r.id, i.ingrediente
FROM recetas r
CROSS JOIN (VALUES ('quinoa'), ('garbanzos'), ('aguacate')) AS i(ingrediente)
WHERE r.nombre = 'Bowl de quinoa y garbanzos'
  AND NOT EXISTS (
    SELECT 1 FROM ingredientes_receta ir
    WHERE ir.receta_id = r.id AND ir.ingrediente = i.ingrediente
  );

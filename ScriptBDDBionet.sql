CREATE DATABASE BioNetDB;
GO

USE BioNetDB;
GO

CREATE TABLE resultados_examenes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    laboratorio_id INT NOT NULL,
    paciente_id VARCHAR(20) NOT NULL,
    tipo_examen VARCHAR(50) NOT NULL,
    resultado VARCHAR(50) NOT NULL,
    fecha_examen DATE NOT NULL,
    CONSTRAINT uq_resultado_unico UNIQUE (paciente_id, tipo_examen, fecha_examen)
);

CREATE TABLE log_cambios_resultados (
    id INT IDENTITY(1,1) PRIMARY KEY,
    operacion VARCHAR(10), -- 'INSERT' o 'UPDATE'
    paciente_id VARCHAR(20),
    tipo_examen VARCHAR(50),
    fecha DATETIME DEFAULT GETDATE()
);

CREATE TRIGGER trg_auditoria_resultados
ON resultados_examenes
AFTER INSERT, UPDATE
AS
BEGIN
    -- Insertar cambios desde la tabla INSERTED
    INSERT INTO log_cambios_resultados (operacion, paciente_id, tipo_examen)
    SELECT 
        CASE 
            WHEN EXISTS (SELECT * FROM inserted EXCEPT SELECT * FROM deleted) 
            THEN 'UPDATE' ELSE 'INSERT' 
        END,
        i.paciente_id,
        i.tipo_examen
    FROM inserted i;
END;


INSERT INTO resultados_examenes (laboratorio_id, paciente_id, tipo_examen, resultado, fecha_examen)
VALUES (101, '1234567890', 'glucosa', '95', '2025-04-10');


SELECT * FROM log_cambios_resultados;
SELECT * FROM resultados_examenes;

INSERT INTO resultados_examenes (laboratorio_id, paciente_id, tipo_examen, resultado, fecha_examen)
VALUES (102, '1234567890', 'glucosa', '98', '2025-04-10');

UPDATE resultados_examenes
SET resultado = '96'
WHERE paciente_id = '1234567890' AND tipo_examen = 'glucosa' AND fecha_examen = '2025-04-10';

SELECT * FROM log_cambios_resultados ORDER BY id DESC;

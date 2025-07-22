INSERT INTO tb_roles SELECT 1 AS role_id, 'ADMIN' AS name WHERE 1 NOT IN (SELECT role_id FROM tb_roles);
INSERT INTO tb_roles SELECT 2 AS role_id, 'BASIC' AS name WHERE 2 NOT IN (SELECT role_id FROM tb_roles);
INSERT INTO tb_roles SELECT 3 AS role_id, 'PRO' AS name WHERE 3 NOT IN (SELECT role_id FROM tb_roles);
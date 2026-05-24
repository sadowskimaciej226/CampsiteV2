-- Skrzynki w SECTOR-A (id: 1)
INSERT INTO electricity_box (box_number, free_electric_contacts_amount, positionx, positiony, sector_id, max_capacity)
VALUES (1, 5, 12, 45, 1, 10);

INSERT INTO electricity_box (box_number, free_electric_contacts_amount, positionx, positiony, sector_id, max_capacity)
VALUES (2, 2, 14, 50, 1, 10);

-- Skrzynki w SECTOR-B (id: 2)
INSERT INTO electricity_box (box_number, free_electric_contacts_amount, positionx, positiony, sector_id, max_capacity)
VALUES (3, 8, 105, 230, 2, 16);

INSERT INTO electricity_box (box_number, free_electric_contacts_amount, positionx, positiony, sector_id, max_capacity)
VALUES (4, 0, 110, 245, 2, 16);

-- Skrzynka w SECTOR-C (id: 3)
INSERT INTO electricity_box (box_number, free_electric_contacts_amount, positionx, positiony, sector_id, max_capacity)
VALUES (5, 4, 15, 15, 3, 6);

-- Skrzynka nieprzypisana do żadnego sektora (sector_id może być NULL, jeśli schemat na to pozwala)
INSERT INTO electricity_box (box_number, free_electric_contacts_amount, positionx, positiony, sector_id, max_capacity)
VALUES (999, 12, 0, 0, NULL, 12);
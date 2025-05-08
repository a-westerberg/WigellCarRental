
-- WIG-4-SJ

-- CUSTOMER
INSERT INTO customers (personal_identity_number, first_name, last_name, email, phone_number, address) VALUES
('19850101-1234', 'Anna', 'Swensson', 'anna.svensson@example.com', '+46701234567', 'Björkgatan 12, 21436 Malmö'),
('19900215-5678', 'Erik', 'Johansson', 'erik.johansson@example.com', '+46702345678', 'Storgatan 45, 11122 Stockholm'),
('19751230-9101', 'Maria', 'Lindberg', 'maria.lindberg@example.com', '+46703456789', 'Kullavägen 9, 43167 Mölndal'),
('19881122-3456', 'Johan', 'Karlsson', 'johan.karlsson@example.com', '+46704567890', 'Industrigatan 27, 60223 Norrköping'),
('19950505-7890', 'Elin', 'Andersson', 'elin.andersson@example.com', '+46705678901', 'Solrosvägen 5, 90347 Umeå');

-- CAR
INSERT INTO cars (make, model, registration_number, price_per_day,status) VALUES
('Volvo', '740', 'ABC123', 999.00,'AVAILABLE'),
('BMW', 'Z8', 'DEF567', 699.00,'BOOKED'),--BOOKED när vi lämnar in
('Tesla', 'Roadster', 'TES123', 899.00,'IN_SERVICE'),
('Audi', 'Quattro', 'AUD456', 749.00,'BOOKED'),
('Kia', 'Ceed', 'KIA890', 549.00,'IN_SERVICE');

-- BOOKING
INSERT INTO orders (booked_at, start_date, end_date, car_id, customer_id, total_price, active, cancelled) VALUES
('2025-02-05', '2025-02-10', '2025-02-14', 1, 1, 3996.00, false,false),
('2025-01-15', '2025-01-22', '2025-01-26', 2, 2, 2796.00, false,false),
('2025-04-01', '2025-04-05', '2025-04-15', 3, 3, 449.50, false,true),
('2025-03-28', '2025-04-01', '2025-04-07', 4, 4, 4494.00, true,false),
('2025-04-05', '2025-04-06', '2025-04-12', 5, 5, 3294.00, true,false),
('2025-04-12', '2025-04-22', '2025-04-30', 4, 3, 5992.00, true,false),
('2025-04-25', '2025-05-03', '2025-05-07', 1, 1, 199.8, false,true),
('2025-04-24', '2025-05-08', '2025-05-17', 2, 2, 6291.00, true,false),
('2025-05-28', '2025-06-02', '2025-06-05', 3, 1, 2697.00, true,false),
('2025-05-30', '2025-06-12', '2025-06-15', 5, 5, 82.35, false,true);
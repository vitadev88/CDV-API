INSERT INTO featured_packages (id, name, price, duration_days, active, sort_order, created_at)
VALUES
  (UUID(), 'Cơ bản',     0,     7,  1, 1, NOW()),
  (UUID(), 'Tiêu chuẩn', 20000, 14, 1, 2, NOW()),
  (UUID(), 'Nâng cao',   35000, 30, 1, 3, NOW());

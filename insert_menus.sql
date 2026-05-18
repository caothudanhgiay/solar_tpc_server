-- Xóa dữ liệu cũ nếu muốn làm lại từ đầu (Tuỳ chọn)
-- TRUNCATE TABLE tso_menu;

-- 1. Insert Menu cấp 1 (Menu gốc)
-- Vì menu_code không còn AUTO_INCREMENT nên ta sẽ tự cấp phát mã: 10, 20, 30, 40...
INSERT INTO tso_menu (menu_code, menu_name, menu_name_eng, menu_url, menu_id_parent, menu_display_order, is_enabled, note, created_at, created_date) VALUES 
(10, 'Trang Chủ', 'Home', '/', NULL, 1, 1, 'Menu chính', 'admin', NOW()),
(20, 'Lắp Đặt', 'Installation', '/menu/installation', NULL, 2, 1, 'Menu chính', 'admin', NOW()),
(30, 'Dịch Vụ', 'Services', '/menu/services', NULL, 3, 1, 'Menu chính, có menu con', 'admin', NOW()),
(40, 'Dự Án', 'Projects', '/menu/projects', NULL, 4, 1, 'Menu chính', 'admin', NOW()),
(50, 'Bảng Giá Nhanh', 'Quick Pricing', '/menu/quick-pricing', NULL, 5, 1, 'Menu chính', 'admin', NOW()),
(60, 'Liên Hệ', 'Contact', '/menu/contact', NULL, 6, 1, 'Menu chính', 'admin', NOW());

-- 2. Insert Menu cấp 2 (Menu con của "Dịch Vụ" - menu_id_parent = 30)
INSERT INTO tso_menu (menu_code, menu_name, menu_name_eng, menu_url, menu_id_parent, menu_display_order, is_enabled, note, created_at, created_date) VALUES 
(31, 'Dịch Vụ Lắp Đặt Hệ Thống Năng lượng Mặt Trời', 'Solar System Installation Service', '/menu/services?category=lap-dat', 30, 1, 1, 'Menu con', 'admin', NOW()),
(32, 'Dịch Vụ O&M (Vận Hành & Bảo Trì) Trọn Gói', 'O&M Service', '/menu/services?category=om', 30, 2, 1, 'Menu con', 'admin', NOW()),
(33, 'Dịch Vụ Vệ Sinh Tấm Pin NLMT', 'Solar Panel Cleaning Service', '/menu/services?category=ve-sinh', 30, 3, 1, 'Menu con', 'admin', NOW()),
(34, 'Dịch vụ Giám Sát & Vận Hành Hệ Thống NLMT bằng Phần mềm SCADA', 'SCADA Monitoring Service', '/menu/services?category=scada', 30, 4, 1, 'Menu con', 'admin', NOW());

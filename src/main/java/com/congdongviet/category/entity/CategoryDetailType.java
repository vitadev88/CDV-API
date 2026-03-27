package com.congdongviet.category.entity;

/**
 * Xác định bảng detail nào sẽ được dùng cho listing thuộc danh mục này.
 * Thêm danh mục mới trong tương lai chỉ cần thêm giá trị vào enum này
 * và tạo handler tương ứng.
 */
public enum CategoryDetailType {
    REAL_ESTATE,
    CAR
}

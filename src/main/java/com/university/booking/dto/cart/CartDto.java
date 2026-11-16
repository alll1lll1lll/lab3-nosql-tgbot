package com.university.booking.dto.cart;

import java.util.List;
import lombok.Data;

@Data
public class CartDto {
    private String personId;
    private List<CartItemDto> items;
    private long ttlSeconds;
}

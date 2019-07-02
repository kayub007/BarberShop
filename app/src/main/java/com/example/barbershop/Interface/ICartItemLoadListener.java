package com.example.barbershop.Interface;

import com.example.barbershop.Database.CartItem;

import java.util.List;

public interface ICartItemLoadListener {
    void onGetAllItemFromCartSuccess(List<CartItem> cartItemList);
}

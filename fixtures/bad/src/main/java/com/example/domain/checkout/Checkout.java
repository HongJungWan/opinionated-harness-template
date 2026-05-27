package com.example.domain.checkout;

// 위반 샘플(애그리거트 경계, 경고): 다른 애그리거트(Cart)의 내부 엔티티를 직접 참조.
import com.example.domain.cart.CartItem;

public class Checkout {
    public String summarize(CartItem item) {
        return "checkout: " + item.sku(); // AR 통해 접근해야 함
    }
}

package com.example.domain.order;

// 위반 샘플(캡슐화): 도메인 엔티티에 @Setter + public setter → 빈약 모델/가변 노출.
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cart {

    private int itemCount;

    // public setter — 행위가 아닌 데이터 노출 (도메인 캡슐화 위반)
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}

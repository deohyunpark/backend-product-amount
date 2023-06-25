package antigravity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "server occur error"),
    // 상품이 없을 경우
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND,"product not founded"),

    // 상품 가격이 조건에 충족되지 않는 경우
    PRODUCT_PRICE_IS_TOO_LOW(HttpStatus.BAD_REQUEST,"product price is too low to use coupon"),
    PRODUCT_PRICE_IS_TOO_HIGH(HttpStatus.BAD_REQUEST,"product price is too high to user coupon"),

    // 보유한 쿠폰이 없을 경우
    COUPON_LIST_IS_EMPTY(HttpStatus.NOT_FOUND,"coupon list is empty"),

    // 상품에 사용가능 한 쿠폰이 아닌경우
    COUPON_IS_NOT_AVAILABLE(HttpStatus.BAD_REQUEST,"coupon is not available"),

    // 해당 프로모션이 없을 경우

    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND,"promotion not founded"),

    ;

    private HttpStatus status;
    private String message;
}

package antigravity.service;

import antigravity.domain.PromotionDTO;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.exception.AntiGravityApplicationException;
import antigravity.exception.ErrorCode;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductRepository;
import antigravity.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final PromotionProductRepository promotionProductRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        // request 의 상품이 없을 경우
        Product product = productRepository.findById(request.getProductId()).orElseThrow( ()->
                new AntiGravityApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s is not founded", request.getProductId())));

        // request 의 상품 가격이 최소/최대 조건에 충족되지 않을 경우
        if (!MinCheck(product.getPrice())) {
            throw new AntiGravityApplicationException(ErrorCode.PRODUCT_PRICE_IS_TOO_LOW);
        } else if (!MaxCheck(product.getPrice())) {
            throw  new AntiGravityApplicationException(ErrorCode.PRODUCT_PRICE_IS_TOO_HIGH);
        }

        // request 의 쿠폰이 없을 경우
        if (request.getCouponIds().length==0) {
            throw new AntiGravityApplicationException(ErrorCode.COUPON_LIST_IS_EMPTY);
        }

        // request 의 쿠폰
        int[] couponIds = request.getCouponIds();
        Integer[] couponList = Arrays.stream(couponIds).boxed().toArray(Integer[]::new);
        List<PromotionDTO> availPromoList = new ArrayList<>();

        // 오늘 날짜
        Date today = new Date();

        log.info("사용가능한 쿠폰 조회( 해당 상품에 사용가능한지 / 쿠폰 날짜가 맞는지 확인 )");
        List<PromotionDTO> availPromo = promotionProductRepository.findServiceAblePromotion(product.getId(), couponList, today).orElseThrow(() ->
                new AntiGravityApplicationException(ErrorCode.COUPON_IS_NOT_AVAILABLE));

        // 상품 원가격
        int OriginPrice = product.getPrice();

        int totalPrice = 0;

        // 프로모션 리스트 만큼 수행을 해야되고, 프로모션의 모든 조건 또한 수행
        // 쿠폰이 한개일때 혹은 여러개일때 일때?
        // 1. 타입체크
        // 2. 쿠폰의 갯수 (기존 가격에서 하나만 적용될때와 적용된 가격에서 또 적용될때 확인 필요)
        // n개의 쿠폰이 있다면 n번을 돌면서 가격변동이 계속 일어남

        for (PromotionDTO p : availPromo) {
            if (!TypeCheck(p)) {
                totalPrice = TypeCoupon(OriginPrice, p.getDiscount_value());
            } else {
                totalPrice = TypeCode(totalPrice, p.getDiscount_value());

            }
        }
        return ProductAmountResponse.builder().name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(product.getPrice() - totalPrice)
                .finalPrice(wonCheck(totalPrice)).build();
    }
    // 최소 / 최대 금액 확인
    public boolean MinCheck(int productPrice) {
        int min = 10000;
        return productPrice >= min;
    }
    // 최소 / 최대 금액 확인
    public boolean MaxCheck(int productPrice) {
        int max = 10000000;
        return productPrice <= max;
    }
    // 천 단위 절삭
    public int wonCheck(int price) {
        return price - (price % 10000);
    }
    // 코드일경우
    public int TypeCode(int price, int salePercent) {
        return (int) (price * ((100 - salePercent) * 0.01d));
    }
    // 쿠폰일경우
    public int TypeCoupon(int price, int salePrice) {
        return price - salePrice ;
    }
    // 프로모션의 타입 판별
    public boolean TypeCheck(PromotionDTO promotion) {
        //프로모션의 타입이 CODE 이면 true 반환
        return promotion.getPromotion_type().equals("CODE");
    }





}

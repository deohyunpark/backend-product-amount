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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    ProductRepository productRepository;
    @MockBean
    PromotionRepository promotionRepository;
    @MockBean
    PromotionProductRepository promotionProductRepository;

    @Test
    @DisplayName("존재하지 않는 상품일 경우 에러반환")
    void test_01() {
        int[] list = {1, 2};
        ProductInfoRequest request = new ProductInfoRequest(2, list);


        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());
        AntiGravityApplicationException exception = Assertions.assertThrows(AntiGravityApplicationException.class
                , () -> productService.getProductAmount(request));

        Assertions.assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자의 쿠폰이 없을경우 에러반환")
    void test_02() {
        int[] list = {};
        ProductInfoRequest request = new ProductInfoRequest(1, list);


        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(new Product(1, "피팅노드상품", 215000)));
        AntiGravityApplicationException exception = Assertions.assertThrows(AntiGravityApplicationException.class
                , () -> productService.getProductAmount(request));

        Assertions.assertEquals(ErrorCode.COUPON_LIST_IS_EMPTY, exception.getErrorCode());
    }
    @Test
    @DisplayName("상품의 최소 금액보다 작은경우 에러반환")
    void test_03() {
        int[] list = {1,2};
        ProductInfoRequest request = new ProductInfoRequest(1, list);


        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(new Product(1, "피팅노드상품", 9000)));
        AntiGravityApplicationException exception = Assertions.assertThrows(AntiGravityApplicationException.class
                , () -> productService.getProductAmount(request));

        Assertions.assertEquals(ErrorCode.PRODUCT_PRICE_IS_TOO_LOW, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품의 최대 금액보다 큰 경우 에러반환")
    void test_04() {
        int[] list = {1,2};
        ProductInfoRequest request = new ProductInfoRequest(1, list);


        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(new Product(1, "피팅노드상품", 1000000000)));
        AntiGravityApplicationException exception = Assertions.assertThrows(AntiGravityApplicationException.class
                , () -> productService.getProductAmount(request));

        Assertions.assertEquals(ErrorCode.PRODUCT_PRICE_IS_TOO_HIGH, exception.getErrorCode());
    }

    @Test
    @DisplayName("쿠폰 사용 기한이 맞지 않거나 상품에 사용할 수 없는 쿠폰일 경우 에러반환")
    void test_05() {
        int[] list = {5};

        ProductInfoRequest request = new ProductInfoRequest(1, list);
        PromotionDTO promotionDTO = new PromotionDTO(1, "COUPON", "30000원 할인쿠폰", "WON", 30000);
        Date today = new Date();
        String StrDate = "20220202";
        String EndDate = "20230505";
        Promotion promotion;
        SimpleDateFormat from = new SimpleDateFormat("yyyyMMdd");
        try {
            Date SDate = from.parse(StrDate);
            Date EDate = from.parse(EndDate);

            promotion = new Promotion(1, "COUPON", "30000원 할인쿠폰", "WON", 30000, SDate,EDate);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(new Product(1, "피팅노드상품", 215000)));
        when(promotionProductRepository.findServiceAblePromotion(request.getProductId(),1,today)).thenReturn(Optional.of(promotionDTO));
        AntiGravityApplicationException exception = Assertions.assertThrows(AntiGravityApplicationException.class
                , () -> productService.getProductAmount(request));

        Assertions.assertEquals(ErrorCode.COUPON_IS_NOT_AVAILABLE, exception.getErrorCode());

    }



}
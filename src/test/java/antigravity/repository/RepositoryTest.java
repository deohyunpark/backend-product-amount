package antigravity.repository;

import antigravity.domain.PromotionDTO;
import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@SpringBootTest
class RepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PromotionProductRepository promotionProductRepository;

    @Test
    void 매핑_테스트_1() {
        Optional<Product> selectP = productRepository.findById(1);
        int price = selectP.get().getPrice();

        Assertions.assertEquals(215000, price);
    }

    @Test
    void 매핑_테스트_2() {
        List<Promotion> list = promotionProductRepository.findAllByProductId(1);
        String type = list.get(0).getPromotion_type();

        Assertions.assertEquals("COUPON",type );

    }
    @Test
    void 매핑_테스트_3() {

        String StrDate = "20230228";
        SimpleDateFormat from = new SimpleDateFormat("yyyyMMdd");
        try {
            Date Date = from.parse(StrDate);
            PromotionDTO promotion = promotionProductRepository.findServiceAblePromotion(1,1,Date).orElseThrow();

                System.out.println(promotion.getPromotion_type());

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
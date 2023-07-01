package antigravity.repository;

import antigravity.domain.PromotionDTO;
import antigravity.domain.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static antigravity.domain.entity.QPromotion.*;
import static antigravity.domain.entity.QPromotionProducts.promotionProducts;


//public interface PromotionProductRepository extends JpaRepository<PromotionProducts, Integer> {
//
//    List<PromotionProducts> findAllByProductId(Product productId);
//
//}
@Repository
public class PromotionProductRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public PromotionProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<Promotion> findAllByProductId(int productId) {
        return queryFactory.selectFrom(promotion)
                .join(promotion).fetchJoin()
                .on(promotionProducts.promotionId.id.eq(productId))
                .fetch();
    }



    public Optional<List<PromotionDTO>> findServiceAblePromotion(Integer productId, Integer[] promoId, Date today) {
        return Optional.ofNullable(queryFactory.select(Projections.fields(PromotionDTO.class, promotion.id, promotion.promotion_type,promotion.name, promotion.discount_type, promotion.discount_value))
                .from(promotion)
                .join(promotionProducts).fetchJoin()
                .on(eqProduct(productId))
                .where(promotion.id.in(promoId).and(isDateBetween(today)))
                .fetch());

    }
    // request 의 product 와 일치하는 product
    public BooleanExpression eqProduct(Integer productId) {
        return productId != null ? promotionProducts.productId.id.eq(productId) : null;
    }

    // epPromo + isDateBetween
    public BooleanExpression isValid(Integer promoId, Date today) {
        return eqPromotion(promoId).and(isDateBetween(today));
    }
    // request 의 couponIds 가 Product 에 사용 가능한 쿠폰 인지 확인
    public BooleanExpression eqPromotion(Integer promoId) {
        return promoId != null ? promotion.id.eq(promoId) : null;
    }
    // 기간 내 사용 가능한 쿠폰 인지 확인
    public BooleanExpression isDateBetween(Date today) {
        BooleanExpression isGoeStartDate = promotion.use_started_at.loe(today);
        BooleanExpression isLoeEndDate = promotion.use_ended_at.goe(today);
        return Expressions.allOf(isGoeStartDate, isLoeEndDate);
    }




}

package antigravity.repository;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class PromotionRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public PromotionRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<Promotion> findById(int id) {
        Promotion findPromotion = em.find(Promotion.class, id);
        return Optional.ofNullable(findPromotion);
    }
}

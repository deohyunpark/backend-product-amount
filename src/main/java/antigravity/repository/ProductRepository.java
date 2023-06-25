package antigravity.repository;

import antigravity.domain.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class ProductRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ProductRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<Product> findById(int id) {
        Product findProduct = em.find(Product.class, id);
        return Optional.ofNullable(findProduct);
    }
}

package com.chris.repository.Impl;

import com.chris.entity.Merchant;
import com.chris.repository.MerchantRepositoryCustom;
import com.chris.utils.GeoUtil;
import com.chris.vo.AllRestaurantsVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class MerchantRepositoryCustomImpl implements MerchantRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<AllRestaurantsVO> fetchByDistance(
            double lng,
            double lat,
            String merchantName,
            Short merchantStatus,
            Double lastDistance,
            Long lastId,
            int pageSize
    ) {
        /*System.out.printf(">> fetchByDistance: lastDistance=%s, lastId=%s, pageSize=%d%n",
                lastDistance, lastId, pageSize);*/
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AllRestaurantsVO> cq = cb.createQuery(AllRestaurantsVO.class);
        Root<Merchant> root = cq.from(Merchant.class);

        // 计算距离
        Expression<Double> dist = cb.function(
                "ST_DistanceSphere", Double.class,
                root.get("location"),
                cb.literal(GeoUtil.makePoint(lng, lat))
        );

        // 构造 SELECT VO
        cq.select(cb.construct(
                AllRestaurantsVO.class,
                root.get("merchantId"),
                root.get("merchantName"),
                root.get("address"),
                root.get("merchantDescription"),
                root.get("merchantImage"),
                root.get("merchantType"),
                root.get("merchantStatus"),
                dist
        ));

        // 筛选条件：名称、状态
        List<Predicate> preds = new ArrayList<>();
        if (merchantName != null) {
            preds.add(cb.like(root.get("merchantName"), "%" + merchantName + "%"));
        }
        if (merchantStatus != null) {
            preds.add(cb.equal(root.get("merchantStatus"), merchantStatus));
        }
        // 距离限制：<= 10 km
        preds.add(cb.le(dist, 10000d));
        // 距离 KeySet 游标过滤
        if (lastDistance != null) {
            Predicate p1 = cb.gt(dist, lastDistance);
            Predicate p2 = cb.and(
                    cb.equal(dist, lastDistance),
                    cb.gt(root.get("merchantId"), lastId)
            );
            preds.add(cb.or(p1, p2));
        }
        cq.where(preds.toArray(new Predicate[0]));

        // 排序：distance ASC, merchantId ASC
        cq.orderBy(cb.asc(dist), cb.asc(root.get("merchantId")));

        TypedQuery<AllRestaurantsVO> query = em.createQuery(cq);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
}

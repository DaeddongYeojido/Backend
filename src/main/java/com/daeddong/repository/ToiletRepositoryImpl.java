package com.daeddong.repository;

import com.daeddong.domain.QToilet;
import com.daeddong.domain.Toilet;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ToiletRepositoryImpl implements ToiletRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Toilet> findNearbyWithFilter(double lat, double lng, double radius,
                                             Toilet.OpenStatus openStatus,
                                             Boolean isDisabled) {
        List<Long> ids = jdbcTemplate.queryForList(
                """
                SELECT id FROM toilets
                WHERE ST_Distance_Sphere(location, ST_SRID(POINT(?, ?), 4326)) <= ?
                ORDER BY ST_Distance_Sphere(location, ST_SRID(POINT(?, ?), 4326))
                """,
                Long.class,
                lng, lat, radius, lng, lat
        );

        if (ids.isEmpty()) return Collections.emptyList();

        QToilet toilet = QToilet.toilet;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(toilet.id.in(ids));

        if (openStatus != null) builder.and(toilet.openStatus.eq(openStatus));
        if (isDisabled != null) builder.and(toilet.isDisabled.eq(isDisabled));

        List<Toilet> result = queryFactory
                .selectFrom(toilet)
                .where(builder)
                .fetch();

        result.sort((a, b) -> Integer.compare(ids.indexOf(a.getId()), ids.indexOf(b.getId())));
        return result;
    }
}

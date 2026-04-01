package com.daeddong.repository;

import com.daeddong.domain.Toilet;
import java.util.List;

public interface ToiletRepositoryCustom {
    List<Toilet> findNearbyWithFilter(double lat, double lng, double radius,
                                      Toilet.OpenStatus openStatus,
                                      Boolean isDisabled);
}

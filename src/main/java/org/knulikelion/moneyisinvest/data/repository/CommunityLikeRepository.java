package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> getCommunityLikeByCommunityIdAndUserId(Long communityId, Long userId);
    List<CommunityLike> getCommunityLikeByCommunityId(Long communityId);
}

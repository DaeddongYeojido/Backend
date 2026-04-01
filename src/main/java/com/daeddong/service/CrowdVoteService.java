package com.daeddong.service;

import com.daeddong.domain.CrowdVote;
import com.daeddong.domain.Toilet;
import com.daeddong.dto.request.CrowdVoteRequest;
import com.daeddong.dto.response.CrowdVoteResponse;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import com.daeddong.repository.CrowdVoteRepository;
import com.daeddong.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrowdVoteService {

    private final CrowdVoteRepository crowdVoteRepository;
    private final ToiletRepository toiletRepository;

    @Value("${crowd.vote.expire-minutes:10}")
    private int expireMinutes;

    @Transactional
    public CrowdVoteResponse vote(Long toiletId, CrowdVoteRequest request) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new DaeddongException(ErrorCode.TOILET_NOT_FOUND));

        Optional<CrowdVote> existing =
                crowdVoteRepository.findByToiletIdAndDeviceId(toiletId, request.getDeviceId());

        CrowdVote vote;
        if (existing.isPresent()) {
            vote = existing.get();
            vote.refresh(request.getLevel(), expireMinutes);
        } else {
            vote = CrowdVote.create(toilet, request.getDeviceId(),
                    request.getLevel(), expireMinutes);
            crowdVoteRepository.save(vote);
        }

        return CrowdVoteResponse.from(vote);
    }

    @Scheduled(fixedDelay = 300_000)
    @Transactional
    public void cleanExpiredVotes() {
        int deleted = crowdVoteRepository.deleteExpired(LocalDateTime.now());
        if (deleted > 0) log.info("[CrowdVote] 만료 투표 {}건 삭제", deleted);
    }
}

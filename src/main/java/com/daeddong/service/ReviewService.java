package com.daeddong.service;

import com.daeddong.domain.Review;
import com.daeddong.domain.Toilet;
import com.daeddong.dto.request.ReviewRequest;
import com.daeddong.dto.response.ReviewResponse;
import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import com.daeddong.global.s3.S3Uploader;
import com.daeddong.repository.ReviewRepository;
import com.daeddong.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private static final String S3_FOLDER = "reviews";

    private final ReviewRepository reviewRepository;
    private final ToiletRepository toiletRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public ReviewResponse createReview(Long toiletId, ReviewRequest request, MultipartFile image) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new DaeddongException(ErrorCode.TOILET_NOT_FOUND));

        // 기기당 화장실 하나에 리뷰 1개 제한
        reviewRepository.findByToiletIdAndDeviceId(toiletId, request.getDeviceId())
                .ifPresent(r -> { throw new DaeddongException(ErrorCode.REVIEW_ALREADY_EXISTS); });

        // 이미지가 있으면 S3 업로드 (선택 사항)
        String imageUrl = (image != null && !image.isEmpty())
                ? s3Uploader.upload(image, S3_FOLDER)
                : null;

        Review review = Review.create(toilet, request.getDeviceId(),
                request.getRating(), request.getContent(), imageUrl);
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public Page<ReviewResponse> getReviews(Long toiletId, Pageable pageable) {
        if (!toiletRepository.existsById(toiletId)) {
            throw new DaeddongException(ErrorCode.TOILET_NOT_FOUND);
        }
        return reviewRepository.findByToiletIdOrderByCreatedAtDesc(toiletId, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public void deleteReview(Long toiletId, Long reviewId, String deviceId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new DaeddongException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getToilet().getId().equals(toiletId)) {
            throw new DaeddongException(ErrorCode.REVIEW_NOT_FOUND);
        }
        if (!review.getDeviceId().equals(deviceId)) {
            throw new DaeddongException(ErrorCode.REVIEW_FORBIDDEN);
        }

        // S3 이미지 삭제 (imageUrl 없으면 내부에서 무시됨)
        s3Uploader.delete(review.getImageUrl());

        reviewRepository.delete(review);
    }
}

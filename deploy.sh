#!/usr/bin/env bash
# deploy.sh - 대똥여지도 백엔드 GCP Cloud Run 배포 스크립트
# 사용법: ./deploy.sh

set -euo pipefail

# ── 설정값 ──────────────────────────────────
PROJECT_ID="daeddong"                        # GCP 프로젝트 ID
REGION="asia-northeast3"                     # 서울 리전
SERVICE_NAME="daeddong-backend"
IMAGE="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"
CLOUD_SQL_INSTANCE="daeddong:asia-northeast3:daeddong-db"
# ────────────────────────────────────────────

echo "▶ Docker 이미지 빌드 & 푸시"
gcloud builds submit --tag "${IMAGE}" .

echo "▶ Cloud Run 배포"
gcloud run deploy "${SERVICE_NAME}" \
  --image "${IMAGE}" \
  --platform managed \
  --region "${REGION}" \
  --allow-unauthenticated \
  --port 8080 \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 10 \
  --add-cloudsql-instances "${CLOUD_SQL_INSTANCE}" \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod" \
  --set-secrets \
    "DB_HOST=daeddong-db-host:latest,\
DB_NAME=daeddong-db-name:latest,\
DB_USERNAME=daeddong-db-username:latest,\
DB_PASSWORD=daeddong-db-password:latest,\
AWS_ACCESS_KEY=daeddong-aws-access-key:latest,\
AWS_SECRET_KEY=daeddong-aws-secret-key:latest,\
S3_BUCKET_NAME=daeddong-s3-bucket:latest,\
FIREBASE_SERVICE_ACCOUNT_PATH=daeddong-firebase-path:latest"

echo "배포 완료!"
gcloud run services describe "${SERVICE_NAME}" \
  --region "${REGION}" \
  --format "value(status.url)"
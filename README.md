# Food Manager Backend

食材・日用品を管理するWebアプリケーションのバックエンドAPIです。

## 機能

- **食材管理API**: CRUD操作、カテゴリ別フィルタリング
- **買い物リストAPI**: CRUD操作、食材ストックへの移動
- **ユーザー認証**: JWT認証、ユーザー登録・ログイン
- **データ分離**: ユーザーごとのデータ分離

## 技術スタック

- Java 17
- Spring Boot 3.1
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT (JSON Web Token)
- Maven

## API エンドポイント

### 認証
| メソッド | パス | 説明 |
|----------|------|------|
| POST | /api/auth/signup | ユーザー登録 |
| POST | /api/auth/signin | ログイン |

### 食材
| メソッド | パス | 説明 |
|----------|------|------|
| GET | /api/foods | 食材一覧取得 |
| POST | /api/foods | 食材追加 |
| PUT | /api/foods | 食材更新 |
| DELETE | /api/foods/{id} | 食材削除 |

### 買い物リスト
| メソッド | パス | 説明 |
|----------|------|------|
| GET | /api/shopping | 買い物リスト取得 |
| POST | /api/shopping | アイテム追加 |
| PUT | /api/shopping | アイテム更新 |
| DELETE | /api/shopping/{id} | アイテム削除 |

### 統計
| メソッド | パス | 説明 |
|----------|------|------|
| GET | /api/stats | ダッシュボード用統計 |

## 開発環境セットアップ

### 前提条件
- Java 17
- Maven
- MySQL 8.0 または Docker

### データベース起動（Docker）

```bash
docker compose up -d mysql
```

### アプリケーション起動

```bash
# 環境変数設定
export DB_USER=your_user
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret

# 起動
./mvnw spring-boot:run
```

### テスト

```bash
./mvnw test
```

## 環境変数

| 変数名 | 説明 | 必須 |
|--------|------|------|
| DB_USER | データベースユーザー名 | Yes |
| DB_PASSWORD | データベースパスワード | Yes |
| JWT_SECRET | JWT署名用シークレット | Yes |

## 本番環境

Docker を使用してデプロイします。

```bash
docker build -t food-manager-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/food_manager \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e JWT_SECRET=your_secret \
  food-manager-backend
```

## CI/CD (GitHub Actions)

GitHub Actionsを使用して、mainブランチへのpush時に自動でビルド・テスト・デプロイを実行します。

### VPS構成

```
VPS ~/food-manager-v2/
├── docker-compose.yml  (手動配置)
├── frontend/           → GitHub: foodmanager-frontend
└── backend/            → GitHub: foodmanager-backend
```

### セットアップ手順

#### 1. ローカルプロジェクトにワークフロー作成

`.github/workflows/ci.yml` を作成

#### 2. VPS側でSSH鍵を作成

```bash
# 鍵を生成
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/id_ed25519_github_actions

# 公開鍵をVPS許可リストに登録
cat ~/.ssh/id_ed25519_github_actions.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

#### 3. GitHub Secretsに登録

リポジトリの **Settings → Secrets and variables → Actions → New repository secret** で以下を登録：

| Name | Value |
|------|-------|
| `VPS_HOST` | VPSのIPアドレス |
| `VPS_USER` | SSHユーザー名 |
| `SSH_PRIVATE_KEY` | 秘密鍵の中身（`cat ~/.ssh/id_ed25519_github_actions`） |
| `JWT_SECRET` | JWT署名用シークレット |

#### 4. ワークフローの内容

```yaml
name: CI and Deploy

on:
  push:
    branches:
      - main
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build and test
        run: mvn -B clean test
        env:
          SPRING_PROFILES_ACTIVE: test
          JWT_SECRET: ${{ secrets.JWT_SECRET }}

  deploy:
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    steps:
      - name: Setup SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/id_ed25519
          chmod 600 ~/.ssh/id_ed25519
          ssh-keyscan -H ${{ secrets.VPS_HOST }} >> ~/.ssh/known_hosts
      - name: Deploy on VPS
        run: |
          ssh ${{ secrets.VPS_USER }}@${{ secrets.VPS_HOST }} << 'EOF'
            set -e
            git config --global --add safe.directory /home/daisuke/food-manager-v2/backend
            cd /home/daisuke/food-manager-v2/backend
            git fetch origin main
            git reset --hard origin/main
            cd /home/daisuke/food-manager-v2
            docker compose up -d --build
          EOF
```

### デプロイフロー

```
push → build & test (CI) → 成功 → deploy (VPS) → docker compose up
```

- `needs: build` により、ビルド・テスト成功後にのみデプロイが実行されます
- `git reset --hard` を使用することで、force push後も安定してデプロイできます

## ディレクトリ構成

```
src/main/java/com/spire/fridge/inventory/
├── config/         # 設定クラス
├── controller/     # REST コントローラー
├── model/          # エンティティ
├── repository/     # データアクセス
├── security/       # 認証・認可
└── service/        # ビジネスロジック
```

## ライセンス

MIT

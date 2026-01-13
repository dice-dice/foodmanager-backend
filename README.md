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

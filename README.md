# Crafting-Simulator

レシピと加工装置に対するtickベースのシミュレーター。

## 作業手順

### 環境構築

```shell
docker compose run --rm flyway migrate ; docker compose up -d
```

### マイグレーション
```shell
docker compose run --rm flyway migrate
```

## 疎通確認

ng serveで起動されたangularのproxy使ってapiにアクセスしてdbにクエリを投げる。

```shell
http://localhost:4200/api/dbtest
```
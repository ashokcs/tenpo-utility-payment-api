# Multipay: Utility Payments Service

## Development

### Docker Compose
```bash
docker-compose -p multipay_utility up
docker run -it --rm --link multipay_utility_postgres_1:postgres --net multipay_utility_default postgres:11-alpine psql -h postgres -U multipay
```

### Run migrations
```bash
cd database
./dbmate up
```

### Spring Run & build
```bash
./gradlew build --continuous
./gradlew bootRun
```

### Dbmate
```bash
dbmate new create_users_table
dbmate up       # migrate and create db if not exists
dbmate migrate  # migrate without creating db
dbmate rollback # roll back the most recent migration
dbmate dump     # generate schema.sql
dbmate wait     # pause until the database is available
```
#!/bin/bash

# docker 접속
# docker exec -it postgresql_ks /bin/bash

set -e

# PostgreSQL의 기본 설정
# psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB"
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -p 5431

#psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
#    BEGIN;
#    -- 여기에서 SQL 초기화 작업을 수행합니다.
#    CREATE TABLE test_table (id SERIAL PRIMARY KEY, name TEXT);
#    INSERT INTO test_table (name) VALUES ('test');
#    -- 수동으로 COMMIT을 수행합니다.
#    COMMIT;
#EOSQL

# 전역 설정이 불가능하기 때문에 AUTOCOMMIT을 FALSE해주자
# \set AUTOCOMMIT off

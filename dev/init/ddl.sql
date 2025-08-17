-- EventLog 테이블
 CREATE TABLE event_log (
     id BIGSERIAL PRIMARY KEY,
     class_name VARCHAR(255),
     method VARCHAR(255),
     line INTEGER,
     message TEXT,
     severity VARCHAR(50),
     topic VARCHAR(255),
     result TEXT
 );

 -- FutureCall 테이블
 CREATE TABLE future_call (
     id BIGSERIAL PRIMARY KEY,
     call_name VARCHAR(255)
 );

 -- 인덱스 생성
 CREATE INDEX idx_future_call_event_log_id ON future_call(event_log_id);

CREATE TABLE topic (
                       id BIGINT NOT NULL,
                       topic VARCHAR(255),
                       partition_count INT,
                       description TEXT,
                       PRIMARY KEY (id)
);

CREATE SEQUENCE topic_seq START 1 INCREMENT BY 50;
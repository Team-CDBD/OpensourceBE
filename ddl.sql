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
    call_name VARCHAR(255),
    event_log_id BIGINT,
    CONSTRAINT fk_future_call_event_log FOREIGN KEY (event_log_id) REFERENCES event_log(id)
);

-- 인덱스 생성
CREATE INDEX idx_future_call_event_log_id ON future_call(event_log_id);
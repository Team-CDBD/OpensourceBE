package com.cdbd.opensource.infrastructure.llm;

/**
 * LLM(거대 언어 모델)으로부터 받은 분석 결과를 담는 불변(immutable) 데이터 객체입니다.
 *
 * @param result LLM이 생성한 분석 결과 문자열 (예시 : "### 1. 원인 분석\n이 오류는 `OrderService` 클래스의 `processOrder` 메소드에서 발생한 `NullPointerException`입니다..."
 */
public record LLMResult(String result) { }

package com.cdbd.opensource.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 레이어드 아키텍쳐를 위한 테스트 코드로 추후 내용 작성되면 삭제 해주세요.
 */
@Tag(name = "Test", description = "테스트용 API 컨트롤러")
@RestController
@RequestMapping("/api/v1")
public class TestController {
	
	@GetMapping("/")
	public String test() {
		return "테스트";
	}

	@Operation(summary = "test", description = "Operation 테스트")
	@GetMapping("/test")
	public String getPosts() {
		return "succ";
	}
}

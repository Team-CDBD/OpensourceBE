package com.cdbd.opensource.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 레이어드 아키텍쳐를 위한 테스트 코드로 추후 내용 작성되면 삭제 해주세요.
 */
@RestController
public class TestController {
	
	@GetMapping("/")
	public String test() {
		return "테스트";
	}
}

package com.cdbd.opensource.presentation;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class PageRequestDto {
    private int page;
    private int size;
    private String sortBy;
    private String direction;
}

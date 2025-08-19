package com.cdbd.opensource.presentation;

import lombok.Builder;

import java.util.List;

@Builder
public class PageResponseDto<T> {
    private List<T> content;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
package com.cdbd.opensource.presentation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PageRequestDto {
    private int page;
    private int size;
    private String sortBy;
    private String direction;
}

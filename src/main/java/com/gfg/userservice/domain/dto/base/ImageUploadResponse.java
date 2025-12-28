package com.gfg.userservice.domain.dto.base;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ImageUploadResponse {
    private String fileId;
    private String url;
    private String thumbnailUrl;
    private String name;
    private Integer height;
    private Integer width;
}

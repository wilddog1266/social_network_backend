package com.example.media_service.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MediaBatchRequest {

    @NotEmpty(message = "Media ids are empty")
    @Size(max = 100, message = "Cannot request more than 100 media items at once")
    private List<Long> ids;
}

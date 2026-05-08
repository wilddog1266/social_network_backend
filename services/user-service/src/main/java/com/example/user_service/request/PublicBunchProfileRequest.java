package com.example.user_service.request;

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
public class PublicBunchProfileRequest {

    @NotEmpty(message = "User ids are empty")
    @Size(max = 100, message = "User ids size must be less than or equal to 100")
    private List<Long> ids;
}

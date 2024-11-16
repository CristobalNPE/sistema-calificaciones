package dev.cnpe.m6finalsc.application.dto.student;

import dev.cnpe.m6finalsc.application.validation.ValidRut;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record StudentRequest(
        @ValidRut
        String rut,

        @NotBlank
        String name,

        @NotBlank
        String address
) {

        public StudentRequest withRut(String rut) {
                return new StudentRequest(rut, this.name, this.address);
        }
}

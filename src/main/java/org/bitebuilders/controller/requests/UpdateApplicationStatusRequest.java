package org.bitebuilders.controller.requests;

import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(
        @NotNull Long statusId
) {}

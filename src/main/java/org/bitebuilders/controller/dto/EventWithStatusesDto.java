package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bitebuilders.model.Event;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventWithStatusesDto {
    private Long id;
    private String title;
    private Event.Status status;
    private List<StatusDto> customStatuses;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusDto {
        private Long id;
        private String name;
        private Integer displayOrder;
    }

    public static EventWithStatusesDto fromEntity(Event event) {
        List<StatusDto> statusDtos = event.getCustomStatuses().stream()
                .map(status -> new StatusDto(
                        status.getId(),
                        status.getName(),
                        status.getDisplayOrder()))
                .toList();

        return new EventWithStatusesDto(
                event.getId(),
                event.getTitle(),
                event.getStatus(),
                statusDtos);
    }
}

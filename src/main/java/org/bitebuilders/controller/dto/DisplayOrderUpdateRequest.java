package org.bitebuilders.controller.dto;

import lombok.*;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor // ОБЯЗАТЕЛЕН для Jackson
@AllArgsConstructor
public class DisplayOrderUpdateRequest {
    private Map<String, Integer> orders;

    public Map<Long, Integer> getParsedOrders() {
        return orders.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getKey()),
                        Map.Entry::getValue
                ));
    }
}

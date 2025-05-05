package org.bitebuilders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.dto.RobotDTO;
import org.bitebuilders.controller.dto.StatusWithRobotsDTO;
import org.bitebuilders.controller.requests.CreateRobotRequest;
import org.bitebuilders.controller.requests.UpdateRobotRequest;
import org.bitebuilders.service.RobotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/robots")
@RequiredArgsConstructor
public class RobotController {
    private final RobotService robotService;

    @GetMapping("/{statusId}")
    public List<RobotDTO> getAll(@PathVariable Long statusId) {
        return robotService.getRobotsByStatusId(statusId);
    }

    @GetMapping("/event/{eventId}")
    public List<StatusWithRobotsDTO> getRobotsByEventId(@PathVariable Long eventId) {
        return robotService.getRobotsGroupedByStatus(eventId);
    }

    @PostMapping("/{statusId}")
    public ResponseEntity<RobotDTO> create(@PathVariable Long statusId, @RequestBody @Valid CreateRobotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(robotService.addRobotToStatus(statusId, request));
    }

    @PutMapping("/{statusId}/{robotId}")
    public RobotDTO update(@PathVariable Long robotId, @RequestBody @Valid UpdateRobotRequest request) {
        return robotService.updateRobot(robotId, request);
    }

    @DeleteMapping("/{statusId}/{robotId}")
    public ResponseEntity<Void> delete(@PathVariable Long statusId, @PathVariable Long robotId) {
        robotService.deleteRobotFromStatus(statusId, robotId);
        return ResponseEntity.noContent().build();
    }
}

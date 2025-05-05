package org.bitebuilders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bitebuilders.controller.dto.RobotDTO;
import org.bitebuilders.controller.dto.StatusWithRobotsDTO;
import org.bitebuilders.controller.requests.CreateRobotRequest;
import org.bitebuilders.exception.CustomNotFoundException;
import org.bitebuilders.model.*;
import org.bitebuilders.repository.ApplicationStatusJdbcRepository;
import org.bitebuilders.repository.RobotJdbcRepository;
import org.bitebuilders.repository.StatusRobotJdbcRepository;
import org.springframework.stereotype.Service;
import org.bitebuilders.controller.requests.CreateRobotRequest;
import org.bitebuilders.controller.requests.UpdateRobotRequest;

import java.util.*;


@Service
@RequiredArgsConstructor
public class RobotService {
    private final RobotJdbcRepository robotRepository;
    private final StatusRobotJdbcRepository statusRobotRepository;
    private final ApplicationStatusJdbcRepository statusJdbcRepository;

    public List<RobotDTO> getRobotsByStatusId(Long statusId) {
        return statusRobotRepository.findRobotsByStatusId(statusId).stream()
                .map(sr -> new RobotDTO(
                        sr.getRobot().getId(),
                        statusId,
                        sr.getRobot().getName(),
                        sr.getRobot().getType(),
                        sr.getRobot().getParameters(),
                        sr.getPosition()
                ))
                .toList();
    }

    public List<StatusWithRobotsDTO> getRobotsGroupedByStatus(Long eventId) {
        List<ApplicationStatus> statuses = statusJdbcRepository.findByEventId(eventId);
        return statuses.stream()
                .map(status -> {
                    List<RobotDTO> robots = getRobotsByStatusId(status.getId());
                    return new StatusWithRobotsDTO(status.getId(), robots);
                })
                .toList();
    }

    public RobotDTO addRobotToStatus(Long statusId, CreateRobotRequest request) {
        Map<String, Object> params = switch (request.type()) {
            case "SEND_MESSAGE" -> Map.of("message", request.message());
            case "SEND_MESSAGE_WITH_LINK" -> Map.of(
                    "message", request.message(),
                    "link", request.link()
            );
            default -> throw new IllegalArgumentException("Неизвестный тип робота");
        };

        Robot robot = new Robot();
        robot.setName(request.name());
        robot.setType(request.type());
        robot.setParameters(params);
        robotRepository.save(robot);

        int position = statusRobotRepository.getNextPosition(statusId);
        statusRobotRepository.save(new StatusRobot(statusId, robot.getId(), position));

        return new RobotDTO(
                robot.getId(),
                statusId,
                robot.getName(),
                robot.getType(),
                robot.getParameters(),
                position
        );
    }

    public RobotDTO updateRobot(Long robotId, UpdateRobotRequest request) {
        Robot robot = robotRepository.findById(robotId)
                .orElseThrow(() -> new CustomNotFoundException("Робот не найден"));

        robot.setName(request.name());
        Map<String, Object> params = switch (robot.getType()) {
            case "SEND_MESSAGE" -> Map.of("message", request.message());
            case "SEND_MESSAGE_WITH_LINK" -> Map.of(
                    "message", request.message(),
                    "link", request.link()
            );
            default -> throw new IllegalArgumentException("Неизвестный тип робота");
        };
        robot.setParameters(params);

        robotRepository.update(robot);

        int position = statusRobotRepository.findPositionByRobotId(robotId);
        Long statusId = statusRobotRepository.findStatusIdByRobotId(robotId);

        return new RobotDTO(
                robot.getId(),
                statusId,
                robot.getName(),
                robot.getType(),
                robot.getParameters(),
                position
        );
    }

    public void deleteRobotFromStatus(Long statusId, Long robotId) {
        statusRobotRepository.delete(statusId, robotId);
        robotRepository.deleteById(robotId);
    }
}


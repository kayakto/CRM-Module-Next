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
        return switch (request.type()) {
            case "SEND_MESSAGE" -> addSendMessageRobot(statusId, request);
            case "SEND_MESSAGE_WITH_LINK" -> addMessageWithLinkRobot(statusId, request);
            case "SEND_TEST" -> addTestRobot(statusId, request);
            default -> throw new IllegalArgumentException("Неизвестный тип робота: " + request.type());
        };
    }

    private RobotDTO addSendMessageRobot(Long statusId, CreateRobotRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Поле 'message' обязательно для SEND_MESSAGE");
        }

        Map<String, Object> params = Map.of("message", request.message());
        return createAndLinkRobot(statusId, request, params);
    }

    private RobotDTO addMessageWithLinkRobot(Long statusId, CreateRobotRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Поле 'message' обязательно для SEND_MESSAGE_WITH_LINK");
        }
        if (request.link() == null || request.link().isBlank()) {
            throw new IllegalArgumentException("Поле 'link' обязательно для SEND_MESSAGE_WITH_LINK");
        }

        Map<String, Object> params = Map.of(
                "message", request.message(),
                "link", request.link()
        );
        return createAndLinkRobot(statusId, request, params);
    }

    private RobotDTO addTestRobot(Long statusId, CreateRobotRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Поле 'message' обязательно для SEND_TEST");
        }
        if (request.link() == null || request.link().isBlank()) {
            throw new IllegalArgumentException("Поле 'link' обязательно для SEND_TEST");
        }

        Map<String, Object> params = Map.of(
                "message", request.message(),
                "link", request.link()
        );
        return createAndLinkRobot(statusId, request, params);
    }

    private RobotDTO createAndLinkRobot(Long statusId, CreateRobotRequest request, Map<String, Object> params) {
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
            case "SEND_MESSAGE" -> extractSendMessageParams(request);
            case "SEND_MESSAGE_WITH_LINK" -> extractMessageWithLinkParams(request);
            case "SEND_TEST" -> extractTestParams(request);
            default -> throw new IllegalArgumentException("Неизвестный тип робота: " + robot.getType());
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

    private Map<String, Object> extractSendMessageParams(UpdateRobotRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Поле 'message' обязательно для SEND_MESSAGE");
        }
        return Map.of("message", request.message());
    }

    private Map<String, Object> extractMessageWithLinkParams(UpdateRobotRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Поле 'message' обязательно для SEND_MESSAGE_WITH_LINK");
        }
        if (request.link() == null || request.link().isBlank()) {
            throw new IllegalArgumentException("Поле 'link' обязательно для SEND_MESSAGE_WITH_LINK");
        }
        return Map.of(
                "message", request.message(),
                "link", request.link()
        );
    }

    private Map<String, Object> extractTestParams(UpdateRobotRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("Поле 'message' обязательно для SEND_TEST");
        }
        if (request.link() == null || request.link().isBlank()) {
            throw new IllegalArgumentException("Поле 'link' обязательно для SEND_TEST");
        }
        return Map.of(
                "message", request.message(),
                "link", request.link()
        );
    }

    public void deleteRobotFromStatus(Long statusId, Long robotId) {
        statusRobotRepository.delete(statusId, robotId);
        robotRepository.deleteById(robotId);
    }
}


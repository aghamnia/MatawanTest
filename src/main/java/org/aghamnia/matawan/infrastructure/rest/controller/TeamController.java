package org.aghamnia.matawan.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aghamnia.matawan.application.service.TeamService;
import org.aghamnia.matawan.domain.model.Team;
import org.aghamnia.matawan.infrastructure.rest.mapper.TeamMapper;
import org.aghamnia.matawan.infrastructure.rest.generated.api.TeamsApi;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.PageTeamResponse;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamCreateRequest;
import org.aghamnia.matawan.infrastructure.rest.generated.dto.TeamResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for team management.
 * Delegates business logic to {@link TeamService} and uses {@link TeamMapper} for DTO conversion.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamController implements TeamsApi {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_SORT_FIELD = "name";
    private static final List<String> ALLOWED_SORT_FIELDS = List.of("name", "acronym", "budget");

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @Override
    public ResponseEntity<PageTeamResponse> _getTeams(Integer page, Integer size, String sort) {
        log.info("GET /api/teams page={}, size={}, sort={}", page, size, sort);
        Pageable pageable = createPageable(page, size, sort);
        Page<Team> teamsPage = teamService.getTeams(pageable);
        PageTeamResponse response = buildPageResponse(teamsPage);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TeamResponse> _addTeam(TeamCreateRequest request) {
        log.info("POST /api/teams name={}", request.getName());
        Team team = teamMapper.toDomain(request);
        Team created = teamService.addTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamMapper.toResponse(created));
    }

    /**
     * Builds a {@link Pageable} from the raw parameters, applying default values
     * and ensuring only allowed sort fields are used.
     */
    private Pageable createPageable(Integer page, Integer size, String sort) {
        int pageIndex = page != null ? page : DEFAULT_PAGE;
        int pageSize = size != null ? size : DEFAULT_SIZE;
        Sort sorting = parseSort(sort);
        return PageRequest.of(pageIndex, pageSize, sorting);
    }

    /**
     * Parses a sort string of the form {@code field,direction} and returns a {@link Sort} object.
     * If the field is not allowed or the string is blank, falls back to the default sort.
     */
    private Sort parseSort(String sort) {
        if (StringUtils.isBlank(sort)) {
            return Sort.by(DEFAULT_SORT_FIELD);
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(property)) {
            log.warn("Unknown sort property '{}', falling back to default '{}'", property, DEFAULT_SORT_FIELD);
            return Sort.by(DEFAULT_SORT_FIELD);
        }
        Sort.Direction direction = parts.length > 1 ?
                Sort.Direction.fromString(parts[1].trim()) :
                Sort.Direction.ASC;
        return Sort.by(direction, property);
    }

    /**
     * Converts a Spring Data {@link Page} of domain {@link Team} objects
     * into the OpenAPI-generated {@link PageTeamResponse}.
     */
    private PageTeamResponse buildPageResponse(Page<Team> page) {
        PageTeamResponse response = new PageTeamResponse();
        response.setContent(page.getContent().stream()
                .map(teamMapper::toResponse)
                .toList());
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }
}
package pl.sadowski.fieldservice;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sectors")
@RequiredArgsConstructor
class SectorController {

    private final SectorService sectorService;

    public record ReleasePeopleRequest(
            @NotNull SectorTag sectorTag,
            @NotNull Integer electricityBoxNumber,
            int amountOfPeople
    ) {}

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.OK)
    public void assignPeople(@RequestBody AssignPeopleRequest request) {
        sectorService.assignPeople(
                request.sectorTag(),
                request.electricityBoxNumber(),
                request.amountOfPeople()
        );
    }

    @PostMapping("/release")
    @ResponseStatus(HttpStatus.OK)
    public void releasePeople(@RequestBody ReleasePeopleRequest request) {
        sectorService.releasePeople(
                request.sectorTag(),
                request.electricityBoxNumber(),
                request.amountOfPeople()
        );
    }
}

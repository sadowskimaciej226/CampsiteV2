package pl.sadowski.fieldservice.field;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sadowski.fieldservice.field.view.CampsiteReportDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
class FiledController {

    private final SectorService sectorService;

    @GetMapping("/fieldReport")
    ResponseEntity<List<CampsiteReportDto>> getCampsiteReport() {
        return ResponseEntity.ok(sectorService.getAllSectorsInfo());
    }

}

package pl.sadowski.fieldservice.field;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sadowski.fieldservice.field.view.SectorTag;

interface SectorRepository extends JpaRepository<Sector, Integer> {

    Sector findSectorBySectorTag(SectorTag sectorTag);
}

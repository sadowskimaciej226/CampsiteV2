package pl.sadowski.fieldservice;

import org.springframework.data.jpa.repository.JpaRepository;

interface SectorRepository extends JpaRepository<Sector, Integer> {

    Sector findSectorBySectorTag(SectorTag sectorTag);
}

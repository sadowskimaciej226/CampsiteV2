package pl.sadowski.fieldservice;

import org.antlr.v4.runtime.misc.NotNull;

public record AssignPeopleRequest(
        @NotNull SectorTag sectorTag,
        @NotNull Integer electricityBoxNumber,
        int amountOfPeople
) {}


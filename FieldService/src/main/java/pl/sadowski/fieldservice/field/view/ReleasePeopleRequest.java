package pl.sadowski.fieldservice.field.view;

import org.antlr.v4.runtime.misc.NotNull;

public record ReleasePeopleRequest(
        @NotNull SectorTag sectorTag,
        @NotNull Integer electricityBoxNumber,
        int amountOfPeople
) {}


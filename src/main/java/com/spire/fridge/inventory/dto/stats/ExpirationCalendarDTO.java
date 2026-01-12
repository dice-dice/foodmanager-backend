package com.spire.fridge.inventory.dto.stats;

import java.time.LocalDate;
import java.util.List;

public class ExpirationCalendarDTO {
    private LocalDate date;
    private List<ExpirationItemDTO> items;

    public ExpirationCalendarDTO() {}

    public ExpirationCalendarDTO(LocalDate date, List<ExpirationItemDTO> items) {
        this.date = date;
        this.items = items;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<ExpirationItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ExpirationItemDTO> items) {
        this.items = items;
    }
}

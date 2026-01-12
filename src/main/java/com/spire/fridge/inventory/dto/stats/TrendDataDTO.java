package com.spire.fridge.inventory.dto.stats;

import java.util.List;

public class TrendDataDTO {
    private List<String> labels;
    private List<Long> added;

    public TrendDataDTO() {}

    public TrendDataDTO(List<String> labels, List<Long> added) {
        this.labels = labels;
        this.added = added;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Long> getAdded() {
        return added;
    }

    public void setAdded(List<Long> added) {
        this.added = added;
    }
}

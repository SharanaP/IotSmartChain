package com.example.sharan.iotsmartchain.fp.models.sort;

import com.example.sharan.iotsmartchain.fp.models.Document;

import java.util.Comparator;

public enum SortingTypes {
    name(new NameComparator()), none(null);

    final private Comparator<Document> comparator;

    SortingTypes(Comparator<Document> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Document> getComparator() {
        return comparator;
    }
}

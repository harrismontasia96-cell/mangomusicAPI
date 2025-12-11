package com.mangomusic.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReportResult {

    private Map<String, Object> data;

    public ReportResult() {
        this.data = new HashMap<>();
    }

    public void addColumn(String columnName, Object value) {
        data.put(columnName, value);
    }

    public Object get(String columnName) {
        return data.get(columnName);
    }

    public String getString(String columnName) {
        Object value = data.get(columnName);
        return value != null ? value.toString() : null;
    }

    public Integer getInt(String columnName) {
        Object value = data.get(columnName);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return Integer.parseInt(value.toString());
    }

    public Long getLong(String columnName) {
        Object value = data.get(columnName);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        return Long.parseLong(value.toString());
    }

    public Double getDouble(String columnName) {
        Object value = data.get(columnName);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        return Double.parseDouble(value.toString());
    }

    public LocalDate getLocalDate(String columnName) {
        Object value = data.get(columnName);
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        return LocalDate.parse(value.toString());
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReportResult{" + "data=" + data + '}';
    }
}
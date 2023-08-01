package com.alarm.technothumb.alarmapplication.calenderr.content;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class ProjectionMap extends HashMap<String, String> {

    public static class Builder {

        private ProjectionMap mMap = new ProjectionMap();

        public Builder add(String column) {
            mMap.putColumn(column, column);
            return this;
        }

        public Builder add(String alias, String expression) {
            mMap.putColumn(alias, expression + " AS " + alias);
            return this;
        }

        public Builder addAll(String[] columns) {
            for (String column : columns) {
                add(column);
            }
            return this;
        }

        public Builder addAll(ProjectionMap map) {
            for (Entry<String, String> entry : map.entrySet()) {
                mMap.putColumn(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public ProjectionMap build() {
            String[] columns = new String[mMap.size()];
            mMap.keySet().toArray(columns);
            Arrays.sort(columns);
            mMap.mColumns = columns;
            return mMap;
        }

    }

    private String[] mColumns;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a sorted array of all column names in the projection map.
     */
    public String[] getColumnNames() {
        return mColumns;
    }

    private void putColumn(String alias, String column) {
        super.put(alias, column);
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        throw new UnsupportedOperationException();
    }
}

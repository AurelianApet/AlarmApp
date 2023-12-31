package com.alarm.technothumb.alarmapplication.calenderr.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NIKUNJ on 01-02-2018.
 */

public class EventColorCache implements Serializable {

    private static final long serialVersionUID = 2L;

    private static final String SEPARATOR = "::";

    private Map<String, ArrayList<Integer>> mColorPaletteMap;
    private Map<String, Integer> mColorKeyMap;

    public EventColorCache() {
        mColorPaletteMap = new HashMap<String, ArrayList<Integer>>();
        mColorKeyMap = new HashMap<String, Integer>();
    }

    /**
     * Inserts a color into the cache.
     */
    public void insertColor(String accountName, String accountType, int displayColor,
                            int colorKey) {
        mColorKeyMap.put(createKey(accountName, accountType, displayColor), colorKey);
        String key = createKey(accountName, accountType);
        ArrayList<Integer> colorPalette;
        if ((colorPalette = mColorPaletteMap.get(key)) == null) {
            colorPalette = new ArrayList<Integer>();
        }
        colorPalette.add(displayColor);
        mColorPaletteMap.put(key, colorPalette);
    }

    /**
     * Retrieve an array of colors for a specific account name and type.
     */
    public int[] getColorArray(String accountName, String accountType) {
        ArrayList<Integer> colors = mColorPaletteMap.get(createKey(accountName, accountType));
        if (colors == null) {
            return null;
        }
        int[] ret = new int[colors.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = colors.get(i);
        }
        return ret;
    }

    /**
     * Retrieve an event color's unique key based on account name, type, and color.
     */
    public int getColorKey(String accountName, String accountType, int displayColor) {
        return mColorKeyMap.get(createKey(accountName, accountType, displayColor));
    }

    /**
     * Sorts the arrays of colors based on a comparator.
     */
    public void sortPalettes(Comparator<Integer> comparator) {
        for (String key : mColorPaletteMap.keySet()) {
            ArrayList<Integer> palette = mColorPaletteMap.get(key);
            Integer[] sortedColors = new Integer[palette.size()];
            Arrays.sort(palette.toArray(sortedColors), comparator);
            palette.clear();
            for (Integer color : sortedColors) {
                palette.add(color);
            }
            mColorPaletteMap.put(key, palette);
        }
    }

    private String createKey(String accountName, String accountType) {
        return new StringBuilder().append(accountName)
                .append(SEPARATOR)
                .append(accountType)
                .toString();
    }

    private String createKey(String accountName, String accountType, int displayColor) {
        return new StringBuilder(createKey(accountName, accountType))
                .append(SEPARATOR)
                .append(displayColor)
                .toString();
    }
}


package com.example.pantrynerd.util;

import java.util.Locale;
import java.util.Map;

// [Assignment 2.3 - matching must cope with simple real-world messiness]
// this class handles the examples from the brief: singular/plural names ("tomato" vs "tomatoes")
// and unit differences (g vs kg, ml vs l, tsp vs tbsp), so a naive exact-string match is not used
public final class IngredNormalization {
    //used to normalize units (example grams to g)
    //and changing from plural to singular (example tomatoes to tomato)
    private IngredNormalization() {

    }

    //this will normalize the units and the different ways people may type units
    private static final Map<String, String> UNIT_ALIASES;
    static {
        UNIT_ALIASES = Map.ofEntries(
                Map.entry("gram", "g"),
                Map.entry("grams", "g"),
                Map.entry("g", "g"),

                Map.entry("kg", "kg"),
                Map.entry("kilogram", "kg"),
                Map.entry("kilograms", "kg"),
                Map.entry("kilos", "kg"),
                Map.entry("kilo", "kg"),

                Map.entry("ml", "ml"),
                Map.entry("milliliter", "ml"),
                Map.entry("milliliters", "ml"),
                Map.entry("millilitre", "ml"),
                Map.entry("millilitres", "ml"),

                Map.entry("l", "l"),
                Map.entry("liter", "l"),
                Map.entry("liters", "l"),
                Map.entry("litre", "l"),

                Map.entry("tsp", "tsp"),
                Map.entry("teaspoon", "tsp"),
                Map.entry("teaspoons", "tsp"),

                Map.entry("tbs", "tbs"),
                Map.entry("tablespoon", "tbs"),
                Map.entry("tablespoons", "tbs"),
                Map.entry("tbsp", "tbs"),

                Map.entry("cup", "cup"),
                Map.entry("cups", "cup"),

                Map.entry("slice", "slice"),
                Map.entry("slices", "slice"),

                Map.entry("piece", "pcs"),
                Map.entry("pieces", "pcs"),
                Map.entry("pcs", "pcs"),
                Map.entry("pc", "pcs"),
                Map.entry("", "pcs")
        );
    }

    //will normalize plural words and make them singular
    //e.g. will turn tomatoes to tomato
    // [Assignment 2.3 - singular/plural] "tomato" and "tomatoes" both become "tomato" before they are compared
    public static String normalizeWords(String text) {
        if (text == null) return "";
        String normalized = text.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (normalized.endsWith("oes") || normalized.endsWith("shes") || normalized.endsWith("ches") || normalized.endsWith("xes") || normalized.endsWith("sses")) {
            //tomatoes to tomato, dishes to dish for example
            normalized = normalized.substring(0, normalized.length() - 2);
        } else if (normalized.endsWith("ies") && normalized.length() > 4) {
            //berries to berry
            normalized = normalized.substring(0, normalized.length() - 3) + "y";
        } else if (normalized.endsWith("s") && !normalized.endsWith("ss") && normalized.length() > 3) {
            //eggs to egg
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    //this will map all the different spelling of units and make it equal to the same shortest form
    //of the unit, for example, kilogram/kilos/kilo will all mean kg
    public static String normalizeUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) return "pcs";
        String normalizedUnit = unit.trim().toLowerCase(Locale.ROOT);
        String alias = UNIT_ALIASES.get(normalizedUnit);
        return alias != null ? alias : normalizedUnit;
    }

    //this will group units into the same type of category
    //example kg and g will be grouped together
    public static String unitCategory(String unit) {
        String normalizedUnit = normalizeUnit(unit);
        switch (normalizedUnit) {
            case "g":
            case "kg":
            case "ml":
            case "l":
            case "tsp":
            case "tbs":
            case "cup":
                return "measure";
            case "pcs":
            case "pc":
            case "piece":
            case "pieces":
            case "slice":
            case "slices":
            default:
                return "count";
        }
    }

    //will convert a quantity to its smallest base unit in its category
    // [Assignment 2.3 - unit differences] e.g. pantry has 1 kg flour, recipe needs 175 g -> 1000 >= 175 so it counts
    public static double toBaseQuantity(double quantity, String unit) {
        if (unit == null) return quantity;

        switch (normalizeUnit(unit)) {
            case "kg":
                return quantity * 1000.0; //kg to g base
            case "l":
                return quantity * 1000.0; //l to ml base
            case "cup":
                return quantity * 240.0;  //cup to ml base
            case "tbs":
                return quantity * 15.0;   //tbsp to ml base
            case "tsp":
                return quantity * 5.0;    //tsp to ml base
            default:
                return quantity;
        }
    }
}

package org.example.model;

public enum WeaponType {
    HAMMER,
    PISTOL,
    KNIFE,
    MACHINE_GUN;

    public static WeaponType fromString(String value) {
        if (value == null) return null;
        try {
            return WeaponType.valueOf(value.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
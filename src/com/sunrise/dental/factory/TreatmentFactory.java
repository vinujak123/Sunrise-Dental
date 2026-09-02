package com.sunrise.dental.factory;

import com.sunrise.dental.model.Treatment;

/**
 * TreatmentFactory – Factory Design Pattern.
 * Encapsulates the creation logic for different types of Treatments.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class TreatmentFactory {

    /**
     * Creates a Treatment object based on category.
     * 
     * @param code treatment code (e.g., GEN-001)
     * @param name treatment name
     * @param category treatment category (e.g., General, Surgery)
     * @param desc description
     * @param duration duration in minutes
     * @param fee base fee
     * @return a new Treatment instance
     */
    public static Treatment createTreatment(String code, String name, String category, String desc, int duration, double fee) {
        Treatment t = new Treatment();
        t.setTreatmentCode(code);
        t.setTreatmentName(name);
        t.setCategory(category);
        t.setDescription(desc);
        
        // Factory logic: Apply rules based on category
        if ("Surgery".equalsIgnoreCase(category)) {
            // Surgery generally takes longer, ensure minimum duration
            t.setDurationMin(Math.max(duration, 60));
        } else if ("Consultation".equalsIgnoreCase(category)) {
            t.setDurationMin(Math.max(duration, 15));
        } else {
            t.setDurationMin(duration);
        }
        
        t.setFee(fee);
        t.setActive(true);
        return t;
    }
}

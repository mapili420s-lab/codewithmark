package com.project.codewithmark.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceTypeEnum {

        WHOLE_BODY("Relaxation Massage",
                        "A full-body therapeutic treatment designed to relax muscles, improve circulation, and relieve overall tension. This service targets all major muscle groups—from head and shoulders down to the legs and feet—using a combination of soothing and deep-pressure techniques. Ideal for reducing stress, promoting relaxation, and restoring balance to the body.",
                        120, 500, "S001"),
        HALF_BODY("Therapeutic Massage",
                        "A focused massage treatment that targets either the upper body (back, shoulders, neck, arms) or lower body (legs and feet). This service is perfect for individuals experiencing localized tension or fatigue. It provides effective relief in a shorter time while still delivering relaxation and muscle recovery benefits.",
                        90, 400, "S002"),
        FOOT_REFLEXOLOGY("Foot Reflexology",
                        "A specialized therapy that applies pressure to specific points on the feet that correspond to different organs and systems in the body. This treatment helps stimulate natural healing, improve circulation, and relieve stress. Ideal for those seeking relaxation and overall wellness through targeted foot therapy.",
                        60, 300, "S003");

        private final String displayName;
        private final String description;
        private final int duration;
        private final int price;
        private final String code;

}

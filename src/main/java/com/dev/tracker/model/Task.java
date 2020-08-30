package com.dev.tracker.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "user")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private TaskStatus status;
    @ManyToOne
    private User user;

    public enum TaskStatus {
        VIEW("View"), IN_PROGRESS("In Progress"), DONE("Done");

        private final String name;

        TaskStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

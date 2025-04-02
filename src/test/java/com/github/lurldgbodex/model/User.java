package com.github.lurldgbodex.model;

import com.github.lurldgbodex.annotations.FieldMapping;

public class User{
        @FieldMapping(column = "id", type = Integer.class)
        private int id;
        @FieldMapping(column = "name")
        private String name;
        @FieldMapping(column = "email")
        private String email;

        private boolean active;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public boolean isActive() {
                return active;
        }

        public void setActive(boolean active) {
                this.active = active;
        }
}

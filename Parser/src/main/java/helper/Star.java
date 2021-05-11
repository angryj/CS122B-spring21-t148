package helper;

import java.util.Objects;

public class Star {

    private String name = "";
    private int birthYear;

    public Star() {}

    public String getName() {
        return name;
    }

    public void setName(String t) {
        this.name = t;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int d) {
        this.birthYear = d;
    }
    public boolean isValid () {
        return getName() != null;
    }

    @Override
    public String toString() {
        String s = "Star Name: " + getName();
        if (getBirthYear() != -1) {
            s += ", Birth Year: " + getBirthYear();
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Star star = (Star) o;
        return getBirthYear() == star.getBirthYear() &&
                getName().equals(star.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBirthYear());
    }
}

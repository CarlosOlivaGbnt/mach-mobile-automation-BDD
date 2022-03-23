package com.mach.core.model;

public class Tax {
    private String country;
    private String dni;

    public Tax() {
        super();
    }

    public Tax(String country, String dni) {
        setCountry(country);
        setDni(dni);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Tax))
            return false;
        if (obj == this)
            return true;
        return this.getCountry().equals(((Tax) obj).getCountry())
                && this.getDni().contains(((Tax) obj).getDni());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode() * 472;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", this.getCountry(), this.getDni());
    }

}

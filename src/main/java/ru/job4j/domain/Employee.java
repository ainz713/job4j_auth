package ru.job4j.domain;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String surname;

    private int inn;

    private Calendar hired;

      @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
      private List<Person> accounts = new ArrayList<>();

    public static Employee of(int id, String name, String surname, int inn, List<Person> accounts) {
        Employee r = new Employee();
        r.id = id;
        r.name = name;
        r.surname = surname;
        r.inn = inn;
        r.setHired(Calendar.getInstance());
        r.setAccounts(accounts);
        return r;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getInn() {
        return inn;
    }

    public void setInn(int inn) {
        this.inn = inn;
    }

    public Calendar getHired() {
        return hired;
    }

    public void setHired(Calendar created) {
        this.hired = created;
    }
    public List<Person> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Person> accounts) {
        this.accounts = accounts;
    }

    public void add(Person person) {
        accounts.add(person);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Employee e = (Employee) o;
        return id == e.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

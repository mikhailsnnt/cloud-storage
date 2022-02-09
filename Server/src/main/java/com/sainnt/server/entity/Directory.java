package com.sainnt.server.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "directories")
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dirs_owners",
            joinColumns = {
                    @JoinColumn(name = "dir_id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_id")
            }
    )
    @ToString.Exclude
    private Set<User> owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Directory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @ToString.Exclude
    private Set<Directory> subDirs;

    @OneToMany(mappedBy = "parentDirectory", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @ToString.Exclude
    private Set<File> files;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Directory directory = (Directory) o;
        return Objects.equals(id, directory.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}


package com.spire.fridge.inventory.entity;

        import jakarta.persistence.*;
        import jakarta.validation.constraints.NotBlank;
        import jakarta.validation.constraints.Size;

        import java.util.HashSet;
        import java.util.Set;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Food> foods = new HashSet<>();

    public Category() {

    }

    public Category(String name) {
        this.name = name;
    } 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
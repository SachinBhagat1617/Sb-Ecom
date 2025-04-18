package com.ecomerce.sb_ecom.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.util.List;

@Entity(name = "categories")
@Data
@NoArgsConstructor // implements constructor with no arguments
@AllArgsConstructor // implements constructor with arguments
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CategoryId;

    @NotBlank
    @Size(min=3,message = "Name must contain atleast 3 characters")
    private String categoryName;

    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL)//the ownership stays with he foreign_key side
    @ToString.Exclude
    public List<Product> products; // mapped_by tells that category will not be the owner and will be taken care by product


/*In your Category model, the constructor is never explicitly called in your code, yet the CategoryId and name values are still being set.
This happens  due to Spring Boot's Jackson-based deserialization mechanism when handling JSON requests in a REST API.*/
//    public Category(long CategoryId, String name) {
//        this.CategoryId = CategoryId;
//        this.name = name;
//    }
//
//    public Category() {
//
//    }
//    // if you remove getter and setter of name then name will not be present in the response and will not be saved in the db this is the importance of getter and setter
//
//    public long getId() {
//        return CategoryId;
//    }
//
//    public void setCategoryId(long CategoryId) {
//        this.CategoryId = CategoryId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {git init
//        this.name = name;
//    }
}

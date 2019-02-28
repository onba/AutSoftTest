package com.example.autsofttest.Persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.*;

@Entity
public class Registry implements Comparable{

    @Id
    @GeneratedValue
    private int id;

    private String title;
    private String content;
    private Date createDate;
    private Date modifyDate;
    @ElementCollection
    @UniqueElements
    private List<String> labels;

    @ManyToMany(mappedBy="registries")
    @JsonIgnore
    private Set<Category> categories = new HashSet();

    public Registry() {
        super();
    }

    public Registry(String title, String content, List<String> labels){
        this.title = title;
        this.content = content;
        this.labels = labels;

        Date now = new Date();
        this.createDate = now;
        this.modifyDate = now;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) { this.categories.add(category);}

    public void deleteCategory(Category category) {this.categories.remove(category);}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {
        Registry other = (Registry)o;
        return  (int) (other.getCreateDate().getTime()-createDate.getTime());
    }
}

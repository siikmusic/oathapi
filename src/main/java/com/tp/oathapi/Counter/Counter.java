package com.tp.oathapi.Counter;

import jakarta.persistence.*;

@Entity
@Table(name = "counter_table")
public class Counter {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;
    private Integer counter;

    public Counter() {
    }

    public Integer getCounter(){
        return this.counter;
    }
    public void setCounter(Integer counter){
        this.counter = counter;
    }
    public void incrementCounter(){
        this.counter = this.counter + 1;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

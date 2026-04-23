package com.soulin.api.color.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="colors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Color {
    @Id
    @Column(name="color_id")
    private Integer colorId;

    @Column(name="color_name", nullable = false, length=50)
    private String colorName;

    @Column(name="color_code", nullable = false, length=9)
    private String colorCode;

    public Color(Integer colorId, String colorName, String colorCode){
        this.colorId=colorId;
        this.colorName=colorName;
        this.colorCode=colorCode;
    }
}

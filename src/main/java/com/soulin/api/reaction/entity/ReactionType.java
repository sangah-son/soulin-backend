package com.soulin.api.reaction.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="reaction_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReactionType {
    @Id
    @Column(name="reaction_type_id")
    private Integer reactionTypeId;

    @Column(name="reaction_name", nullable = false, length=50)
    private String reactionName;

    @Column(name="reaction_text", nullable = false, length = 100)
    private String reactionText;

    public ReactionType(Integer reactionTypeId, String reactionName, String reactionText){
        this.reactionTypeId=reactionTypeId;
        this.reactionName=reactionName;
        this.reactionText=reactionText;
    }
}

package com.Alvaro_Elorza_Santander_SBT.model.basic;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


/**
 * The base entity for all entities that need an id
 */
@MappedSuperclass
@Getter @Setter
public class BaseEntity{
	/**
	 * The id for all entities not nullable 
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    @NotNull(message = "Id_Not_Null")
    private Long id;

}

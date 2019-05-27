package com.Alvaro_Elorza_Santander_SBT.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.Alvaro_Elorza_Santander_SBT.model.basic.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "Account")
public class Account extends BaseEntity{

	@Column(name = "Name",length = 100, nullable = false)
	@NotNull(message = "Name_Not_Null")
	@Size(min = 1, max = 100, message = "Name_Size_Error")
	private String name;

	@Column(name = "Balance", nullable = false,precision=2)
	@NotNull(message = "Balance_Not_Null")
	private Double balance;
}

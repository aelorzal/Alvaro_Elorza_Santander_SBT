package com.account.Alvaro_Elorza_Santander_SBT.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name = "Account")

@ApiModel(description = "All details about the Account. ")
public class Account {
	@Id
	@GeneratedValue
	@Column(name = "Id", nullable = false)
    @ApiModelProperty(notes = "The database generated Account Id")
	private Long id;

	@Column(name = "Name",length = 100, nullable = false)
    @ApiModelProperty(notes = "The database generated Account Name")
	private String name;

	@Column(name = "Balance", nullable = false)
    @ApiModelProperty(notes = "The database generated Account Balance")
	private Double balance;
}

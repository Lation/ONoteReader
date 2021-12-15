package com.whz.reader.dto;

import java.util.UUID;

/**
 * Stream DTO which refers to an oNote Stream. An stream is a lane in which
 * events reside.
 * 
 * @author Timon Schwalbe
 */
public class Stream {

	private UUID id;
	private String name;

	public Stream() {
	}

	public Stream(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Stream [id=" + id + ", name=" + name + "]";
	}

}
package com.whz.reader.dto;

import java.util.UUID;

/**
 * Flow DTO which refers to an oNote Flow/Arrow. These arrows link different
 * notes together in a unidirectional (one-way) relationship usually from the
 * left to the right as time passes.
 * 
 * The Flow IDs correspond to Placements and not directly Events, Commands or
 * Read Models!
 * 
 * @author Timon Schwalbe
 */
public class Flow {

	private UUID from;
	private UUID to;

	public Flow() {
	}

	public Flow(UUID from, UUID to) {
		this.from = from;
		this.to = to;
	}

	public UUID getFrom() {
		return from;
	}

	public void setFrom(UUID from) {
		this.from = from;
	}

	public UUID getTo() {
		return to;
	}

	public void setTo(UUID to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "Flow [from=" + from + ", to=" + to + "]";
	}

}

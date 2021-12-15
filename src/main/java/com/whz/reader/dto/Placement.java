package com.whz.reader.dto;

import java.util.UUID;

/**
 * Placement DTO which refers a note position in oNote. A note is placed on a
 * specific index inside a specific lane depending on the type of note. The
 * Placement DTO contains all the information as to where precisely the note is
 * located.
 * 
 * 'INTERFACE' are placed in 'AUDIENCE'.
 * 
 * 'COMMANDS' and 'READ_MODEL' are placed in 'TIMELINE'.
 * 
 * 'EVENT' are placed in 'STREAM'
 * 
 * If the LaneType is Stream or Audience, it will have a laneId associated with
 * the corresponding lane. A timeline does not have a laneId since only a single
 * timeline can exist for all the commands and read models.
 * 
 * @author Timon Schwalbe
 */
public class Placement {

	private UUID id;
	private int index;
	private NoteType noteType;
	private UUID noteId;
	private LaneType laneType;
	private UUID laneId;

	/**
	 * List of all note types used by oNote.
	 */
	public enum NoteType {
		INTERFACE, COMMAND, READ_MODEL, EVENT
	}

	/**
	 * List of all lane types used by oNote. While there can be multiple audiences
	 * and stream there can only be a single timeline containing the commands and
	 * read model.
	 */
	public enum LaneType {
		AUDIENCE, TIMELINE, STREAM
	}

	public Placement() {
	}

	public Placement(UUID id, int index, NoteType noteType, UUID noteId, LaneType laneType, UUID laneId) {
		this.id = id;
		this.index = index;
		this.noteType = noteType;
		this.noteId = noteId;
		this.laneType = laneType;
		this.laneId = laneId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public NoteType getNoteType() {
		return noteType;
	}

	public void setNoteType(NoteType noteType) {
		this.noteType = noteType;
	}

	public UUID getNoteId() {
		return noteId;
	}

	public void setNoteId(UUID noteId) {
		this.noteId = noteId;
	}

	public LaneType getLaneType() {
		return laneType;
	}

	public void setLaneType(LaneType laneType) {
		this.laneType = laneType;
	}

	public UUID getLaneId() {
		return laneId;
	}

	public void setLaneId(UUID laneId) {
		this.laneId = laneId;
	}

	@Override
	public String toString() {
		return "Placement [id=" + id + ", index=" + index + ", noteType=" + noteType + ", noteId=" + noteId
				+ ", laneType=" + laneType + ", laneId=" + laneId + "]";
	}

}

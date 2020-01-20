package be.leonix.sandbox.model;

import static be.leonix.sandbox.model.MovieMongoMapping.DESCRIPTION;
import static be.leonix.sandbox.model.MovieMongoMapping.EXTERNAL_ID;
import static be.leonix.sandbox.model.MovieMongoMapping.ID;
import static be.leonix.sandbox.model.MovieMongoMapping.TAGS;
import static be.leonix.sandbox.model.MovieMongoMapping.TITLE;

import java.util.Set;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class defines a movie as persisted in Mongo.
 * 
 * @author leonix
 */
public class Movie {
	
	@JsonProperty(ID)
	private ObjectId id;
	@JsonProperty(TITLE)
	private String title;
	@JsonProperty(DESCRIPTION)
	private String description;
	@JsonProperty(TAGS)
	private Set<String> tags;
	@JsonProperty(EXTERNAL_ID)
	private String externalId;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<String> getTags() {
		return tags;
	}
	
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}

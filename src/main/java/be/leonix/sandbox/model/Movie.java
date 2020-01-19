package be.leonix.sandbox.model;

import static be.leonix.sandbox.model.MovieMongoMapping.DESCRIPTION;
import static be.leonix.sandbox.model.MovieMongoMapping.EXTERNAL_ID;
import static be.leonix.sandbox.model.MovieMongoMapping.MOVIE_TYPE_ALIAS;
import static be.leonix.sandbox.model.MovieMongoMapping.TAGS;
import static be.leonix.sandbox.model.MovieMongoMapping.TITLE;

import java.util.Set;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class defines a movie as persisted in Mongo.
 * 
 * @author leonix
 */
@TypeAlias(MOVIE_TYPE_ALIAS)
public class Movie {
	
	@Field(EXTERNAL_ID)
	@JsonProperty(EXTERNAL_ID)
	private String externalId;
	
	@Field(TITLE)
	@JsonProperty(TITLE)
	private String title;
	
	@Field(DESCRIPTION)
	@JsonProperty(DESCRIPTION)
	private String description;
	
	@Field(TAGS)
	@JsonProperty(TAGS)
	private Set<String> tags;
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String externalId) {
		this.externalId = externalId;
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
}

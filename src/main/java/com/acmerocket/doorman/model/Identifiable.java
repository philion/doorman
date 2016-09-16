package com.acmerocket.doorman.model;


/**
 * 
 * @author <a href="mailto:paul@snupi.com">Paul Philion</a> 
 */
public interface Identifiable {
	
    /**
     * Get the ID of the entity. This is the key be which the entity is persisted.
     * @return
     */
	public String getId();
	
	public void setId(String id);
}

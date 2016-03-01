package com.ahajri.btalk.data.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence")
public class SequenceId {

    @Id
    private String id;

    private long seq;

    /**
     * @return the id
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return the seq
     */
    public long getSeq() {
	return seq;
    }

    /**
     * @param seq
     *            the seq to set
     */
    public void setSeq(long seq) {
	this.seq = seq;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "SequenceId [id=" + id + ", seq=" + seq + "]";
    }

}

/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.tutorial.topsongs.jaxb;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

// the root POJO for the tree data structure for a hit song
@XmlRootElement
public class TopSong {
	public TopSong() {
		super();
	}

	private String    songId;
	private String    title;
	private Artist    artist;
	private Weeks     weeks;
	private Album     album;
	private String    released;
	private Formats   formats;
	private String    recorded;
	private Genres    genres;
	private Lengths   lengths;
	private String    label;
	private Writers   writers;
	private Producers producers;

	private Element   descr;

	public void setSongId(String songId) {
		this.songId = songId;
	}
	public String getSongId() {
		return songId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Artist getArtist() {
		return artist;
	}
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	public Album getAlbum() {
		return album;
	}
	public void setAlbum(Album album) {
		this.album = album;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Weeks getWeeks() {
		return weeks;
	}
	public void setWeeks(Weeks weeks) {
		this.weeks = weeks;
	}
	public String getReleased() {
		return released;
	}
	public void setReleased(String released) {
		this.released = released;
	}
	public Formats getFormats() {
		return formats;
	}
	public void setFormats(Formats formats) {
		this.formats = formats;
	}
	public String getRecorded() {
		return recorded;
	}
	public void setRecorded(String recorded) {
		this.recorded = recorded;
	}
	public Genres getGenres() {
		return genres;
	}
	public void setGenres(Genres genres) {
		this.genres = genres;
	}
	public Lengths getLengths() {
		return lengths;
	}
	public void setLengths(Lengths lengths) {
		this.lengths = lengths;
	}
	public Writers getWriters() {
		return writers;
	}
	public void setWriters(Writers writers) {
		this.writers = writers;
	}
	public Producers getProducers() {
		return producers;
	}
	public void setProducers(Producers producers) {
		this.producers = producers;
	}

	// a fragment of marked-up text (XML mixed content)
	@XmlAnyElement
	public Element getDescr() {
		return descr;
	}
	public void setDescr(Element description ) {
		this.descr = description;
	}

	@Override
	public String toString() {
		return "Topsong [title=" + title + ", artist=" + artist + ", weeks="
				+ weeks + ", album=" + album + ", released=" + released
				+ ", formats=" + formats + ", recorded=" + recorded
				+ ", genres=" + genres + ", lengths=" + lengths + ", label="
				+ label + ", writers=" + writers + ", producers=" + producers
				+ ", descr=" + stringifyDescr() + "]";
	}
	public String stringifyDescr() {
	    String xmlString = null;

	    try
	    {
	      // Set up the output transformer
	      TransformerFactory transfac = TransformerFactory.newInstance();
	      Transformer trans = transfac.newTransformer();
	      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	      trans.setOutputProperty(OutputKeys.INDENT, "no");

	      // Print the DOM node
	      StringWriter sw = new StringWriter();
	      StreamResult result = new StreamResult(sw);
	      DOMSource source = new DOMSource(descr);
	      trans.transform(source, result);
	      xmlString = sw.toString();
   	    }
	    catch (Exception e)
	    {
	      xmlString = "";
	    } 

		return xmlString;
	}
}

package org.fastcatgroup.analytics.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lupfeliz
 *
 */
public class XMLResponseWriter implements ResponseWriter {
	protected static Logger logger = LoggerFactory.getLogger(XMLResponseWriter.class);
	private static final String ARRAY_DEFAULT_ITEM_NAME = "item";
	
	enum NODE_TYPE { OBJECT, ARRAY };
	Document document;
	Element root;
	Element currentElement;
	List<String> arrayName;
	List<NODE_TYPE> types;
	boolean beautify;
	Writer w;
	
	public XMLResponseWriter(Writer w, String rootName) {
		this(w, rootName, false);
	}
	
	public XMLResponseWriter(Writer w, String rootName, boolean beautify) {
		this.w = w;
		document = DocumentHelper.createDocument();
		root = document.addElement(rootName);
		currentElement = root;
		this.beautify = beautify;
		types = new ArrayList<NODE_TYPE>();
		arrayName = new ArrayList<String>();
	}
	
	@Override
	public ResponseWriter object() throws ResultWriterException {
		if(types.size()>0 && types.get(types.size()-1)==NODE_TYPE.ARRAY) {
			types.add(NODE_TYPE.OBJECT);
			currentElement = currentElement.addElement(arrayName.get(arrayName.size()-1));
		}
		return this;
	}

	@Override
	public ResponseWriter endObject() throws ResultWriterException {
		if(types.size()>1 && types.get(types.size()-2)==NODE_TYPE.ARRAY) {
			types.remove(types.size()-1);
			currentElement = currentElement.getParent();
		}
		return this;
	}

	@Override
	public ResponseWriter array() throws ResultWriterException {
		return array(ARRAY_DEFAULT_ITEM_NAME);
	}
	
	@Override
	public ResponseWriter array(String arrayName) throws ResultWriterException {
		if(types.size()>0 && types.get(types.size()-1)==NODE_TYPE.ARRAY) {
			currentElement = currentElement.addElement(this.arrayName.get(
					this.arrayName.size()-1));
		}
		types.add(NODE_TYPE.ARRAY);
		this.arrayName.add(arrayName);
		return this;
	}

	@Override
	public ResponseWriter endArray() throws ResultWriterException {
		currentElement = currentElement.getParent();
		types.remove(types.size()-1);
		this.arrayName.remove(arrayName.size()-1);
		return this;
	}

	@Override
	public ResponseWriter key(String key) throws ResultWriterException {
		currentElement = currentElement.addElement(key);
		return this;
	}

	@Override
	public ResponseWriter value(Object obj) throws ResultWriterException {
		
		if(types.size()!=0 && types.get(types.size()-1)==NODE_TYPE.ARRAY) {
			currentElement = currentElement.addElement(arrayName.get(arrayName.size()-1));
		} else {
			
		}
		if(obj == null){
			currentElement = currentElement.addText("");
		}else{
			currentElement = currentElement.addText(obj.toString());
		}
		currentElement = currentElement.getParent();
		return this;
	}
	
	@Override
	public String toString() {
		if(beautify) {
			StringWriter sw = new StringWriter();
			try {
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform
			    (new StreamSource(new StringReader(document.asXML())), 
			     new StreamResult(sw));
				return sw.toString();
			} catch (TransformerConfigurationException e) {
			} catch (TransformerException e) {
			} finally {
			}
		} else {
			return document.asXML();
		}
		return "";
	}

	@Override
	public void done() {
		if(w != null){
			try {
				w.write(toString());
			} catch (IOException e) {
				logger.error("write error", e);
			}
		
			try {
				w.close();
			} catch (IOException e) {
				logger.error("close error", e);
			}
		}
	}
}
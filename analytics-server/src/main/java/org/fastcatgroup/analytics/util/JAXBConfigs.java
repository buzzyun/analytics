package org.fastcatgroup.analytics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.fastcatgroup.analytics.io.BytesDataInput;
import org.fastcatgroup.analytics.io.BytesDataOutput;
import org.fastcatgroup.analytics.io.DataInput;
import org.fastcatgroup.analytics.io.DataOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAXBConfigs {
	private static final Logger logger = LoggerFactory.getLogger(JAXBConfigs.class);
	
	public static <T> T readConfig(File file, Class<T> jaxbConfigClass) throws JAXBException {
		logger.debug("readConfig file >> {}, {}", file.getAbsolutePath(), file.exists());
		
		if(!file.exists()){
			return null;
		}
		
		InputStream is = null;
		try{
			is = new FileInputStream(file);
			logger.debug("read config file={}, {}", file.getName(), is);
			T config = readConfig(is, jaxbConfigClass);
//			logger.debug("read config {}, {}", config, file.getName());
			return config;
		}catch(Exception e){
			logger.error("JAXBConfig file error "+file.getAbsolutePath(), e);
			throw new JAXBException(e);
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
	}
	
	public static <T> T readConfig(InputStream is, Class<T> jaxbConfigClass) throws JAXBException {
		if(is == null){
			return null;
		}
		JAXBContext context = JAXBContext.newInstance(jaxbConfigClass);
		
		Unmarshaller unmarshaller = context.createUnmarshaller();

		T config = (T) unmarshaller.unmarshal(is);
		return config;
	}
	
	public static <T> T readFrom(DataInput is, Class<T> jaxbConfigClass) throws JAXBException {
		if(is == null){
			return null;
		}
		int size = 0;
		try {
			size = is.readVInt();
		} catch (IOException e) {
			throw new JAXBException(e);
		}
		byte[] array = new byte[size];
		try {
			is.readBytes(array, 0, size);
		} catch (IOException e) {
			throw new JAXBException(e);
		}
		
		BytesDataInput bytesInput = new BytesDataInput(array, 0, size);
		
		return readConfig(bytesInput, jaxbConfigClass);
	}
	
	
	public static <T> void writeConfig(File file, Object jaxbConfig, Class<T> jaxbConfigClass) throws JAXBException {
		OutputStream os = null;
		try{
			if (!file.exists()) {
				logger.debug("create {}", file.getAbsolutePath());
				file.createNewFile();
			}
			
			os = new FileOutputStream(file);
			writeRawConfig(os, jaxbConfig, jaxbConfigClass);
		}catch(IOException e){
			throw new JAXBException(e);
		}finally{
			if(os != null){
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
		
	}
	public static <T> void writeRawConfig(OutputStream os, Object jaxbConfig, Class<T> jaxbConfigClass) throws JAXBException {
		writeRawConfig(os, jaxbConfig, jaxbConfigClass, false);
	}
	public static <T> void writeRawConfig(OutputStream os, Object jaxbConfig, Class<T> jaxbConfigClass, boolean removeXmlDeclaration) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(jaxbConfigClass);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		logger.debug("removeXmlDeclaration!! {}", removeXmlDeclaration);
		if(removeXmlDeclaration){
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
//			marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
		}
		marshaller.marshal(jaxbConfig, os);
	}
	
	public static <T> void writeTo(DataOutput os, Object jaxbConfig, Class<T> jaxbConfigClass) throws JAXBException {
		try{
			BytesDataOutput bytesOutput = new BytesDataOutput();
			writeRawConfig(bytesOutput, jaxbConfig, jaxbConfigClass);
			int byteSize = (int) bytesOutput.position();
			os.writeVInt(byteSize);
			os.writeBytes(bytesOutput.array(), 0, byteSize);
		}catch(IOException e){
			throw new JAXBException(e);
		}
	}
}

package com.lowcodeminds.plugins.template.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;

import java.util.*;

public class TemplateServices {
	
	private static final Log LOG = LogFactory.getLog(TemplateServices.class);
	

	public static Long createDocument(String documentName, String extension, DocType fileType,
			PluginContext context, ContentService contentService) {
		Document document = null;
		Long generatedDocument = null;
		try {
			document = new Document(context.getSaveInFolder(), documentName, extension);
			document.setState(ContentConstants.STATE_ACTIVE_PUBLISHED);
			document.setFileSystemId(ContentConstants.ALLOCATE_FSID);
			generatedDocument = contentService.create(document, ContentConstants.UNIQUE_NONE);
			if (fileType  == DocType.DOC) {
				context.setNewGeneratedDocument( contentService.getVersion(generatedDocument, ContentConstants.VERSION_CURRENT).getId());

			}
			if (fileType == DocType.PDF) {
				context.setNewPDFGeneratedDocument( contentService.getVersion(generatedDocument, ContentConstants.VERSION_CURRENT)
						.getId());

			}
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("createDocument Exception : " + e);
			LOG.error("createDocument Exception : " + e);
		}

		return generatedDocument;

	}
	
	
	public static  Map<String , String[]> extactTags(String tagName,PluginContext context) {
		
		String escapedHeaderTagsString = StringEscapeUtils.unescapeJava(context.getJsonFormatTags());
        Map<String , String[]> values = new  HashMap<>();
		String[] fieldNames;
		String[] fieldValues;
		try {
			final JSONObject obj = new JSONObject(escapedHeaderTagsString);
			final JSONArray headerTags = obj.getJSONArray(tagName);
			fieldNames = new String[headerTags.length()];
			fieldValues = new String[headerTags.length()];
			if ((obj.has(tagName)) && !obj.getJSONArray(tagName).isEmpty()) {
				for (int i = 0; i < headerTags.length(); ++i) {
	
					JSONObject obj1 = headerTags.getJSONObject(i);
					fieldNames[i] = obj1.getString("field");
					fieldValues[i] = obj1.getString("value");
				}
			
			}
			values.put(TemplateConstants.FIELDS, fieldNames);
			values.put(TemplateConstants.VALUES, fieldValues);
		}catch(Exception e) {
			LOG.info( tagName + " Not found in the Json string.");
		}
		
		return values;
	}
	

	public static SmartServiceException createException(Throwable t ,Class clazz) {
		LOG.error(t);
		SmartServiceException.Builder b = new SmartServiceException.Builder(clazz, t);
		return b.build();
	}
	
	

}

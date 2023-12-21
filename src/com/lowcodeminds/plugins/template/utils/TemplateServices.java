package com.lowcodeminds.plugins.template.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.appiancorp.suiteapi.common.exceptions.AppianStorageException;
import com.appiancorp.suiteapi.common.exceptions.StorageLimitException;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.content.ContentUploadOutputStream;
import com.appiancorp.suiteapi.content.exceptions.DuplicateUuidException;
import com.appiancorp.suiteapi.content.exceptions.InsufficientNameUniquenessException;
import com.appiancorp.suiteapi.content.exceptions.InvalidContentException;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;

public class TemplateServices {

	private static final Logger LOG = Logger.getLogger(TemplateServices.class);

	public static Long createDocument(String documentName, String extension, DocType fileType, PluginContext context,
			ContentService contentService) {
		Document document = null;
		Long generatedDocument = null;
		try {
			document = new Document(context.getSaveInFolder(), documentName, extension);
			document.setState(ContentConstants.STATE_ACTIVE_PUBLISHED);
			document.setFileSystemId(ContentConstants.ALLOCATE_FSID);
			generatedDocument = contentService.create(document, ContentConstants.UNIQUE_NONE);
			if (fileType == DocType.DOC) {
				context.setNewGeneratedDocument(
						contentService.getVersion(generatedDocument, ContentConstants.VERSION_CURRENT).getId());

			}
			if (fileType == DocType.PDF) {
				context.setNewPDFGeneratedDocument(
						contentService.getVersion(generatedDocument, ContentConstants.VERSION_CURRENT).getId());

			}
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("createDocument Exception : " + e);
			LOG.error("createDocument Exception : " + e);
		}

		return generatedDocument;

	}

	public static Map<String, String[]> extactTags(String tagName, PluginContext context) {

		String escapedHeaderTagsString = StringEscapeUtils.unescapeJava(context.getJsonFormatTags());
		Map<String, String[]> values = new HashMap<>();
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
		} catch (Exception e) {
			LOG.info(tagName + " Not found in the Json string.");
		}

		return values;
	}

	public static SmartServiceException createException(Throwable t, Class clazz) {
		LOG.error("There appears to be a problem processing document templating");
		LOG.error(t);
		SmartServiceException.Builder b = new SmartServiceException.Builder(clazz, t);
		return b.build();
	}

	public static ContentUploadOutputStream uploadDocumentToAppian(ContentService cs, String documentName,
			String extension, PluginContext context, ContentService contentService)
			throws Exception, InvalidContentException, StorageLimitException, InsufficientNameUniquenessException,
			DuplicateUuidException, AppianStorageException {

		// Create document object
		Document document = new Document(context.getSaveInFolder(), documentName, extension);
		document.setState(ContentConstants.STATE_ACTIVE_PUBLISHED);
		document.setFileSystemId(ContentConstants.ALLOCATE_FSID);
		return cs.uploadDocument(document, ContentConstants.UNIQUE_NONE);

	}
   /**
    * This method return InputStrem for com.aspose.words.Document object.
    * @param doc
    * @param format
    * @return
    * @throws Exception
    */
	public static InputStream getDocumentStream(com.aspose.words.Document doc, int format) throws Exception {

		// Create a new memory stream.
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// Save the document to stream.
		doc.save(outStream, format);
		// Convert the document to byte form.
		byte[] docBytes = outStream.toByteArray();
		// The bytes are now ready to be stored/transmitted.
		// Now reverse the steps to load the bytes back into a document object.
		ByteArrayInputStream inStream = new ByteArrayInputStream(docBytes);
		return inStream;

	}

}

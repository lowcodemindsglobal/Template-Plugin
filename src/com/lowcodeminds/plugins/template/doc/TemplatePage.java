package com.lowcodeminds.plugins.template.doc;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.common.exceptions.AppianStorageException;
import com.appiancorp.suiteapi.common.exceptions.InvalidVersionException;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.content.exceptions.InvalidContentException;
import com.appiancorp.suiteapi.knowledge.Document;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Field;
import com.aspose.words.FieldIncludeText;
import com.aspose.words.FieldType;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateException;

public abstract class TemplatePage {

	public final String extensionValue = "docx";
	public final String extensionPDFValue = "pdf";

	public ContentService contentService = null;
	public ContentService tmpContentService = null;
	com.aspose.words.Document doc;

	public PluginContext context;

	public Long templateFile;

	public static String[] fieldNames;
	public static String[] fieldValues;
	
	//Hold appian include TEXT file name
	public String appianDocDisplayName = "";
	

	public String getAppianDocDisplayName() {
		return appianDocDisplayName;
	}

	public void setAppianDocDisplayName(String appianDocDisplayName) {
		this.appianDocDisplayName = appianDocDisplayName;
	}

	private static final Log LOG = LogFactory.getLog(TemplatePage.class);

	TemplatePage(final ContentService contentService, final PluginContext context, com.aspose.words.Document doc,
			final ContentService tmpContentService) {
		this.contentService = contentService;
		this.context = context;
		this.doc = doc;
		this.tmpContentService = tmpContentService;

	}

	TemplatePage() {

	}

	public void createDoOcument() {

	}

	public void apply() throws SmartServiceException {

		if (context.isErrorOccured()) {
			return;
		}

		applyTemplating();
	}

	protected abstract void applyTemplating() throws SmartServiceException;

	public abstract void cleanUp();

	public Document download(Long[] document) throws TemplateException {

		Document doc = null;
		for (int i = 0; i < document.length; i++) {
			try {
				doc = contentService.download(document[i], ContentConstants.VERSION_CURRENT, false)[0];
			} catch (Exception e) {
				throw new TemplateException(context, e);
			}
		}

		return doc;

	}

	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

	/**
	 * Get the relevent HEADER include Document  from the document list.
	 * @param documents
	 * @return
	 * @throws Exception
	 * @throws InvalidContentException
	 * @throws InvalidVersionException
	 */
	public String getIncudeDocument(Long[] documents)
			throws Exception, InvalidContentException, InvalidVersionException {

		String matchDocPath = "";
		for (int i = 0; i < documents.length; i++) {

			Document appianDoc = contentService.download(documents[i], ContentConstants.VERSION_CURRENT, false)[0];
			LOG.debug(i + " : " + appianDoc.getDisplayName());
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText fIT = (FieldIncludeText) field;
					if (fIT.getSourceFullName().contains(appianDoc.getDisplayName())) {
						LOG.info("Found match : " + fIT.getSourceFullName());
						matchDocPath = appianDoc.getInternalFilename();
						appianDocDisplayName = appianDoc.getDisplayName();
						break;

					}

				}
			}

		}

		return matchDocPath;

	}
	
	/**
	 * Get the relevent HEADER include Document  from the document list.
	 * @param documents
	 * @return
	 * @throws Exception
	 * @throws InvalidContentException
	 * @throws InvalidVersionException
	 */
	public InputStream getIncludeStream(Long[] documents)
			throws Exception, InvalidContentException, InvalidVersionException {

		String matchDocPath = "";
		InputStream ins = null;
		for (int i = 0; i < documents.length; i++) {

			Document appianDoc = contentService.download(documents[i], ContentConstants.VERSION_CURRENT, false)[0];
			LOG.debug(i + " : " + appianDoc.getDisplayName());
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText fIT = (FieldIncludeText) field;
					if (fIT.getSourceFullName().contains(appianDoc.getDisplayName())) {
						LOG.info("Found match : " + fIT.getSourceFullName());
						//matchDocPath = appianDoc.getInternalFilename();
						ins = getDocumentInputStream(appianDoc);
						appianDocDisplayName = appianDoc.getDisplayName();
						break;

					}

				}
			}

		}

		return ins;

	}
	
	public InputStream getDocumentInputStream(Document doc) throws Exception {
		
		InputStream ins = null;
		if(doc !=null)
			ins = doc.getInputStream();
		return ins;
		
		
	}
	
	public InputStream getDocumentInputStream(Long docId) throws Exception {
		
		Document appianDoc = contentService.download(docId, ContentConstants.VERSION_CURRENT, false)[0];
		
		InputStream ins = null;
		if(appianDoc !=null)
			ins = appianDoc.getInputStream();
		return ins;
		
		
	}

}

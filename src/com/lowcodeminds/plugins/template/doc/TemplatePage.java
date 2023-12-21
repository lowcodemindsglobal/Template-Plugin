package com.lowcodeminds.plugins.template.doc;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;

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

	// Hold appian include TEXT file name
	private String appianDocDisplayName = "";

	public File tempFile;

	public String getAppianDocDisplayName() {
		return appianDocDisplayName;
	}

	public void setAppianDocDisplayName(String appianDocDisplayName) {
		this.appianDocDisplayName = appianDocDisplayName;
	}

	private static final Logger LOG = Logger.getLogger(TemplatePage.class);

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

	/** This is  to clean up temporary file resources */
	public abstract void cleanUp();


	public static boolean empty(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}


	/**
	 * This method is used to get the  inputStream  for matching  document name. This will compare the
	 * include Text fileName in master document  against the given list {@code Long[] documents}
	 * ex :{INCLUDETEXT "H:\\IncText\\PlotLet.doc"  \* MERGEFORMAT }
	 * 
	 * @param documents
	 * @return
	 * @throws Exception
	 * @throws InvalidContentException
	 * @throws InvalidVersionException
	 */
	public InputStream getIncludeFileStream(Long[] documents)
			throws Exception, InvalidContentException, InvalidVersionException {

		InputStream ins = null;
		for (int i = 0; i < documents.length; i++) {

			Document appianDoc = contentService.download(documents[i], ContentConstants.VERSION_CURRENT, false)[0];
			LOG.debug(i + " : " + appianDoc.getDisplayName());
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText fIT = (FieldIncludeText) field;
					if (fIT.getSourceFullName().contains(appianDoc.getDisplayName())) {
						LOG.info("Found match : " + fIT.getSourceFullName());
						ins = getDocumentInputStream(appianDoc);
						appianDocDisplayName = appianDoc.getDisplayName();
						break;
					}
				}
			}
		}
		return ins;
	}

	/**
	 * This method is used to get inputStrem for Appian Document object
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	private InputStream getDocumentInputStream(Document doc) throws Exception {
		InputStream ins = null;
		if (doc != null)
			ins = doc.getInputStream();
		return ins;

	}

	/**
	 * This method is used to get InputStream for a given appian document ID
	 * 
	 * @param docId - This is the appian document ID
	 * @return - InputStrem
	 * @throws Exception
	 */

	public InputStream getDocumentInputStream(Long docId) throws Exception {
		Document appianDoc = contentService.download(docId, ContentConstants.VERSION_CURRENT, false)[0];
		InputStream ins = null;
		if (appianDoc != null)
			ins = appianDoc.getInputStream();
		return ins;

	}

}

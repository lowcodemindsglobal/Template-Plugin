package com.lowcodeminds.plugins.template.doc;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Field;
import com.aspose.words.FieldIncludeText;
import com.aspose.words.FieldType;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class HeaderTemplate extends TemplatePage {

	public static String headerTagName = "HeaderTags";

	public final String tempDocNameValue = "tempDocument";
	public final String tempDocExtensionValue = "doc";

	private static final Logger LOG = Logger.getLogger(HeaderTemplate.class);

	public HeaderTemplate(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);
	}

	@Override
	/**
	 * This method return immediately if error reported in previous steps , if no
	 * header documents and no json header tags are given.
	 * Steps:
	 *   1. Get InputStream  for header template
	 *   2. Create Aspose document object 
	 *   3. Execute mail merge
	 *   4. Save document as temporary file
	 *   5. Process main document  template INCLUDE  Text field 
	 */
	public void applyTemplating() throws SmartServiceException {

		if (context.isErrorOccured()) {
			LOG.info("Header Template will not be processed  due to error");
			return;
		}
		Long[] documents = context.getHeaderDocuments();
		if (documents == null) {
			LOG.info("No Header Documents is given.Stop processing HeaderDocuments Template processing");
			return;
		}

		Map<String, String[]> map = TemplateServices.extactTags(headerTagName, context);
		if (map.size() == 0) {
			LOG.info(" No json tag array found  for " + headerTagName);
			return;
		}
		fieldNames = map.get(TemplateConstants.FIELDS);
		fieldValues = map.get(TemplateConstants.VALUES);

		try (InputStream ins = getIncludeFileStream(documents);){
			
			if (ins == null) {
				LOG.info("No FIELD_INCLUDE_TEXT  found for HEADER ");
				return;
			} else
				LOG.info(" FIELD_INCLUDE_TEXT  found for HEADER :");

			com.aspose.words.Document headerdoc = new com.aspose.words.Document(ins);
			headerdoc.getMailMerge().execute(fieldNames, fieldValues);
			LOG.info("creating temporary file for header Document processing");
			tempFile = File.createTempFile("tmp", ".doc");
		

			if (tempFile == null) {
				LOG.info("Fail to create Temporary File");
				context.setErrorOccured(true);
				context.setErrorMessage("Error when processing header template. Fail to create Temporary File");
				return;
			}
			String headerFilePath = tempFile.getAbsolutePath();
			LOG.debug("For Header Template Temp file created at: " + tempFile.getAbsolutePath());
			headerdoc.save(tempFile.getAbsolutePath());
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText iT = (FieldIncludeText) field;

					if (!empty(headerFilePath) && iT.getSourceFullName().contains(getAppianDocDisplayName())) {
						iT.setSourceFullName(headerFilePath);
					}
					//break;
				}

			}

		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing header template");
			LOG.error("Exception in Header Template processing ", e);
			throw TemplateServices.createException(e, getClass());
		}

	}

	@Override
	public void cleanUp() {
		try {
			if (tempFile != null) {
				tempFile.delete();
			}
		} catch (Exception e) {
			LOG.error("Exception in Header Template clean up ");
			context.setErrorOccured(true);
			context.setErrorMessage("Error when clening up header resources");
		}

	}

}

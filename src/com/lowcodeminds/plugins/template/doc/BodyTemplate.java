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

public class BodyTemplate extends TemplatePage {

	public static String embedBodyTagName = "EmbedBodyTags";

	public final String tempDocNameValue = "tempDocument";
	public final String tempDocExtensionValue = "doc";

	private static final Logger LOG = Logger.getLogger(BodyTemplate.class);

	public BodyTemplate(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);

	}

	@Override
	public void applyTemplating() throws SmartServiceException {

		Long[] documents = context.getEmbedBodyDocuments();
		if (documents == null) {
			LOG.info("No embedBodyDocuments is given.Stop processing Body Template processing");
			return;
		}
		Map<String, String[]> map = TemplateServices.extactTags(embedBodyTagName, context);
		if (map.size() == 0) {
			LOG.info("No json tag array found  for " + embedBodyTagName);
			return;
		}

		fieldNames = map.get(TemplateConstants.FIELDS);
		fieldValues = map.get(TemplateConstants.VALUES);

		try (InputStream ins = getIncludeFileStream(documents);){
			
			if (ins != null) {
				LOG.info("No FIELD_INCLUDE_TEXT  found for BODY ");
				return;
			} else
				LOG.info("FIELD_INCLUDE_TEXT  found for BODY : ");

			com.aspose.words.Document bodyDoc = new com.aspose.words.Document(ins);
			bodyDoc.getMailMerge().execute(fieldNames, fieldValues);

			LOG.debug("creating temporary file ");
			tempFile = File.createTempFile("tmp", ".doc");
			// tempFile.deleteOnExit();

			if (tempFile == null) {
				LOG.info("Temp file is null");
			}
			String tmpPath = tempFile.getAbsolutePath();
			bodyDoc.save(tmpPath);
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText iT = (FieldIncludeText) field;

					if (!empty(tmpPath) && iT.getSourceFullName().contains(getAppianDocDisplayName())) {
						iT.setSourceFullName(tmpPath);
					}
					//break;
				}

			}

		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing body template");
			LOG.error("Exception in Body Template processing ", e);
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
			LOG.error("Exception in Body Template clean up ", e);
			LOG.error(e);

		}

	}

}

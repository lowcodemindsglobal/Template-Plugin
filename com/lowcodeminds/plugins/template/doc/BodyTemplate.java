package com.lowcodeminds.plugins.template.doc;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.process.exceptions.SmartServiceException;
import com.aspose.words.Field;
import com.aspose.words.FieldIncludeText;
import com.aspose.words.FieldType;
import com.lowcodeminds.plugins.template.utils.DocType;
import com.lowcodeminds.plugins.template.utils.PluginContext;
import com.lowcodeminds.plugins.template.utils.TemplateConstants;
import com.lowcodeminds.plugins.template.utils.TemplateServices;

public class BodyTemplate extends TemplatePage {

	public static String embedBodyTagName = "EmbedBodyTags";

	Long bodyCreatedDocument;

	public final String tempDocNameValue = "tempDocument";
	public final String tempDocExtensionValue = "doc";

    private static final Log LOG = LogFactory.getLog(BodyTemplate.class);
	
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


		try {

			String path = getIncudeDocument(documents);
			
			if(empty(path)) {
				LOG.info("No FIELD_INCLUDE_TEXT  found for BODY ");
				return;
			}else
				LOG.info("FIELD_INCLUDE_TEXT  found for BODY : " + path);

			com.aspose.words.Document bodyDoc = new com.aspose.words.Document(path);
			bodyDoc.getMailMerge().execute(fieldNames, fieldValues);

			bodyCreatedDocument = TemplateServices.createDocument(tempDocNameValue, tempDocExtensionValue, DocType.DOC,
					context, tmpContentService);
			String tmpPath = tmpContentService.getInternalFilename(bodyCreatedDocument);
			bodyDoc.save(tmpPath);
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText iT = (FieldIncludeText) field;

					if (!empty(tmpPath) && iT.getSourceFullName().contains(appianDocDisplayName)) {
						iT.setSourceFullName(tmpPath);
					}
					break;
				}

			}

		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing body template");
			LOG.error("Exception in Body Template processing " ,e);
			throw TemplateServices.createException(e, getClass());

		}

	}

	@Override

	public void cleanUp() {
		try {
			if (bodyCreatedDocument != null) {
				contentService.delete(bodyCreatedDocument, true);
			}
		} catch (Exception e) {
			LOG.error("Exception in Body Template clean up " ,e);
			LOG.error(e);
		
		}

	}

}

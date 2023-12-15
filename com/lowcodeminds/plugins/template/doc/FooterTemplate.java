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

public class FooterTemplate extends TemplatePage{
	
	
	public static String footerTagName = "FooterTags";
    public final String tempDocNameValue = "tempDocument";
	public final String tempDocExtensionValue = "doc";

	
	Long footerCreatedDocument;
	private static final Log LOG = LogFactory.getLog(FooterTemplate.class);
	
	
	public FooterTemplate(ContentService contentService, PluginContext context, com.aspose.words.Document doc,
			ContentService tmpContentService) {
		super(contentService, context, doc, tmpContentService);
		
	}

	@Override
	public void applyTemplating() throws SmartServiceException {
		Long[] documents= context.getFooterDocuments();
		if(documents == null ) {
			LOG.info("No FooterDocuments is given.Stop processing FooterDocuments Template processing");
			return;
		}
		
		 Map<String , String[]> map = TemplateServices.extactTags(footerTagName,context);
		 if(map.size()== 0) {
			LOG.info(" No json tag array found  for "+ footerTagName ); 
			return ;
		 }
		 
		 fieldNames = map.get(TemplateConstants.FIELDS);
		 fieldValues = map.get(TemplateConstants.VALUES);

			
		try {
			
			String path = getIncudeDocument(documents);
			if(empty(path)) {
				LOG.info("No FIELD_INCLUDE_TEXT  found for FOOTER ");
				return;
			}
			else
				LOG.info(" FIELD_INCLUDE_TEXT  found for FOOTER :" + path);
	
			com.aspose.words.Document footerDoc = new com.aspose.words.Document(path);
			footerDoc.getMailMerge().execute(fieldNames, fieldValues);
			
			footerCreatedDocument = TemplateServices.createDocument(tempDocNameValue, tempDocExtensionValue, DocType.DOC,context,tmpContentService);
			String headerFilePath = tmpContentService.getInternalFilename(footerCreatedDocument);
			footerDoc.save(headerFilePath);
			for (Field field : doc.getRange().getFields()) {
				if (field.getType() == FieldType.FIELD_INCLUDE_TEXT) {
					FieldIncludeText iT = (FieldIncludeText) field;

					if (!empty(headerFilePath) && iT.getSourceFullName().contains(appianDocDisplayName)) {
						iT.setSourceFullName(headerFilePath);
					}
					break;
				}

			}
	
		} catch (Exception e) {
			context.setErrorOccured(true);
			context.setErrorMessage("Error when processing footer template");
			LOG.error("Exception in Footer Template processing " ,e);
			throw TemplateServices.createException(e, getClass());

		}

		
		
	}

	@Override
	public void cleanUp() {
		try {
			if (footerCreatedDocument != null) {
				contentService.delete(footerCreatedDocument, true);
			}
		}catch(Exception e) {
			LOG.error("Exception in Footer Template clean up ");
		}
		
	}

}
